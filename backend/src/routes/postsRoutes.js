import express from "express";
import { db_pool } from "../config/db.js";

const router = express.Router();

// CREATE a new post (andriod studio to mysql)
router.post("/", async (req, res) => {
    try {
        const { profile_id, title, content } = req.body;

        const [result] = await db_pool.query(
            "INSERT INTO Posts (profile_id, title, content) VALUES (?, ?, ?)",
            [profile_id, title, content]
        );

        res.status(201).json({
            message: "Post created",
            postId: result.insertId
        });
    } catch (error) {
        console.error("POST ERROR:", error);
        res.status(500).json({ error: error.message });
    }
});
// GET all posts (Mysql to andriod studio)

router.get("/", async (req, res) => {

    try {
        // Join with your users/profiles table to get the username
        const sql = `
                SELECT p.*, pr.username 
                FROM Posts p 
                JOIN profile pr ON p.profile_id = pr.profile_id 
                `;
        const [rows] = await db_pool.query(sql);

        console.log(rows[0].postId);
        res.json(rows);
    } catch (error) {
        res.status(500).json({ error: "Database error" });
    }
});


//to UPDATE posts
router.put("/:id", async (req, res) => {
    const postId = req.params.id;
    const { title, content } = req.body;

    await db_pool.query(
        "UPDATE Posts SET title = ?, content = ? WHERE postId = ?",
        [title, content, postId]
    );

    res.json({ message: "Post updated" });
});

//delete reply
router.delete("/:postId/replies/:id", async (req, res) => {
    const replyId = req.params.id;
    try {
        const [result] = await db_pool.query(
            "DELETE FROM replies WHERE id = ?",
            [replyId]
        );
        if (result.affectedRows === 0) {
            return res.status(404).json({ message: "Reply not found" });
        }
        res.json({ message: "Reply deleted" });
    } catch (error) {
        console.error("REPLY DELETE ERROR:", error);
        res.status(500).json({ error: error.message });
    }
});

// DELETE a post
router.delete("/:id", async (req, res) => {
    const postId = req.params.id;

    try {
        const [result] = await db_pool.query(
            "DELETE FROM Posts WHERE postId = ?",
            [postId]
        );
        if (result.affectedRows === 0) {
            return res.status(404).json({ message: "Post not found" });
        }
        res.status(200).json({ message: "Post deleted successfully" });
    } catch (error) {
        console.error("Error deleting post:", error);
        res.status(500).json({ error: "Failed to delete post" });
    }
});

// GET all replies for a post
router.get("/:postId/replies", async (req, res) => {
    try {
        const { postId } = req.params;

        const sql = `
            SELECT r.*, pr.username
            FROM replies r
            JOIN profile pr ON r.profile_id = pr.profile_id
            WHERE r.postId = ?
            ORDER BY r.timestamp ASC
        `;

        const [rows] = await db_pool.query(sql, [postId]);

        res.json(rows);
    } catch (error) {
        console.error("REPLY GET ERROR:", error);
        res.status(500).json({ error: error.message });
    }
});

// CREATE a reply for a post
router.post("/:postId/replies", async (req, res) => {
    try {
        const { postId } = req.params;
        const { profile_id, content } = req.body;

        if (!profile_id || !content) {
            return res.status(400).json({ message: "Missing userId or content" });
        }

        const [result] = await db_pool.query(
            "INSERT INTO replies (postId, profile_id, content) VALUES (?, ?, ?)",
            [postId, profile_id, content]
        );

        res.status(201).json({ message: "Reply added", replyId: result.insertId });
    } catch (error) {
        console.error("REPLY POST ERROR:", error);
        res.status(500).json({ error: error.message });
    }
});

export default router;