import express from "express";
import { db_pool } from "../config/db.js";

const router = express.Router();

router.get("/pastdata", async (req, res) => {
    try {
        const [rows] = await db_pool.querey(
            "SELECT weight FROM health_data ORDER BY date"
        );

        res.json(rows);
    }
    catch (error) {
        console.error(error);
        res.status(500).json({ message: "Database Error"});
    }

});

export default router;