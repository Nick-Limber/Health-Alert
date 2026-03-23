import { db_pool } from "../config/db.js";
import argon2 from "argon2";
import { generateToken } from "../utils/generateToken.js";

export const upgradeUser = async (req, res) => {
    const { userId } = req.body;

    if (!userId) {
        return res.status(400).json({ status: "error", message: "Missing userId" });
    }

    try {
        const [result] = await db_pool.query(
            "UPDATE users SET plan = 'premium' WHERE id = ?",
            [userId]
        );

        res.json({ status: "success" });
    } catch (err) {
        console.error(err);
        res.status(500).json({ status: "error" });
    }
};