import express from "express";
import argon2 from "argon2";
import { db_pool } from "../config/db.js";

const router = express.Router();

router.delete("/delete-account", async (req, res) => {
    const { email, password } = req.body;

    console.log("DELETE ACCOUNT REQUEST RECEIVED");
    console.log(`Target email: ${email}`);

    try {
        // serches by email first
        const [rows] = await db_pool.query(
            "SELECT profile_id FROM `profile` WHERE email = ?", 
            [email]
        );

        console.log("Rows found:", rows.length);

        if (rows.length > 0) {
            const validPassword = await argon2.verify(rows[0].password, password);
            console.log("Is the password valid?", validPassword);
        }

        //exits if no email is found
        if (rows.length === 0) { 
            console.log("Delete failed: Email not found:");
            return res.status(401).json({ error: "Invalid credentials" });
        }

        const user = rows[0];

        //compares password to hasehed password in database
        const validPassword = await argon2.verify(user.password, password);

        if (!validPassword) {
            console.log("Delete failed: Incorrect password");
            return res.status(401).json({ error: "Invalid credentials" });
        }

        const masterId = user.profile_id;
        console.log(`Credentials verified for ID: ${masterId}. Deleting...`);

        const connection = await db_pool.getConnection();

        //proceeds with deletion
        try {
            await connection.beginTransaction();

            await connection.query("DELETE FROM personal_information WHERE profile_id = ?",
                [masterId]);

            await connection.query("DELETE FROM diets WHERE profile_id = ?",
                [masterId]);

            await connection.query("DELETE FROM exercise WHERE profile_id = ?",
                [masterId]);
        
            await connection.query("DELETE FROM `profile` WHERE profile_id = ?",
                [masterId]);
        
            await connection.commit();
            console.log("SUCCESS: Account deleted for profile_id:", masterId);

            res.status(200).json({ message: "Account and data has been deleted permanently."});

        } catch (transactionError) {
        await connection.rollback();
        throw transactionError;
    } finally {
        connection.release();
    }
    } catch (err) {
        console.log("DATABASE ERROR:", err);
        res.status(500).json({ error: "Server error during deletion" })
    }
});

export default router;