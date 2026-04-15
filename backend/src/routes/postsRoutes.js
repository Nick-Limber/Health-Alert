import express from "express";
import { db_pool } from "../config/db.js";

const router = express.Router();

// CREATE a new post (andriod studio to mysql)
router.post("/", async (req, res) => {
    try {
        const { userId, title, content } = req.body;

        const [result] = await db_pool.query(
            "INSERT INTO Posts (userId, title, content) VALUES (?, ?, ?)",
            [userId, title, content]
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
        const [rows] = await db_pool.query("SELECT * FROM Posts");
        res.json(rows);
    } catch (error) {
        console.error(error);
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
        const [rows] = await db_pool.query(
            "SELECT * FROM replies WHERE postId = ? ORDER BY timestamp ASC",
            [postId]
        );
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
        const { userId, content } = req.body;

        if (!userId || !content) {
            return res.status(400).json({ message: "Missing userId or content" });
        }

        const [result] = await db_pool.query(
            "INSERT INTO replies (postId, userId, content) VALUES (?, ?, ?)",
            [postId, userId, content]
        );

        res.status(201).json({ message: "Reply added", replyId: result.insertId });
    } catch (error) {
        console.error("REPLY POST ERROR:", error);
        res.status(500).json({ error: error.message });
    }
});

export default router;