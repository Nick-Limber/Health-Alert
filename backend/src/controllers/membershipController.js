import { db_pool } from "../config/db.js";
import argon2 from "argon2";
import { generateToken } from "../utils/generateToken.js";

export const toggleMembership = async (req, res) => {
    const { userId } = req.body;

    if (!userId) {
        return res.status(400).json({ status: "error", message: "Missing userId" });
    }

    try {
        // 1. Get current plan
        const [rows] = await db_pool.query(
            "SELECT plan FROM users WHERE id = ?",
            [userId]
        );

        if (rows.length === 0) {
            return res.status(404).json({ status: "error", message: "User not found" });
        }

        const currentPlan = rows[0].plan;

        // 2. Toggle plan
        const newPlan = currentPlan === "premium" ? "free" : "premium";

        // 3. Update DB
        await db_pool.query(
            "UPDATE users SET plan = ? WHERE id = ?",
            [newPlan, userId]
        );

        res.json({ status: "success", plan: newPlan });

    } catch (err) {
        console.error(err);
        res.status(500).json({ status: "error" });
    }
};

export const getMembership = async (req, res) => {
    const userId = req.params.id;

    try {
        const [rows] = await db_pool.query(
            "SELECT plan FROM users WHERE id = ?",
            [userId]
        );

        if (rows.length === 0) {
            return res.status(404).json({ status: "error" });
        }

        res.json({ status: "success", plan: rows[0].plan });

    } catch (err) {
        console.error(err);
        res.status(500).json({ status: "error" });
    }
};
