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
        // Match to SQL table name
        const [rows] = await db_pool.query("SELECT * FROM Posts");
        res.json(rows);
    } catch (error) {
        console.error(error);
        res.status(500).json({ error: "Database error" });
    }
});

export default router;