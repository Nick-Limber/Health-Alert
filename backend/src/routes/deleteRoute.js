import express from "express";
import { db_pool } from "../config/db.js";

const router = express.Router();

router.delete('/delete-account', async (req, res) => {
    const { email, password } = req.body;

    console.log("DELETE ACCOUNT REQUEST RECEIVED");
    console.log(`Target email: ${email}`);

    try {
        const [profileRows] = await db_pool.query(
            "SELECT profile_id FROM `profile` WHERE email = ? AND password = ?", 
            [email, password]
        );

        if (profileRows.length === 0) { 
            console.log("Delete failed: Invalid credentials for email/password:",);
            return res.status(401).json({ error: "Invalid credentials" });
        }

        const masterId = profileRows[0].profile_id;
        console.log(`Credentials verified. Proceeding to delete data for profile_id: ${masterId}`);

        const connection = await db_pool.getConnection();

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
        console.log("ERROR IN DELETION ROUTE:");
        console.error(err);
        res.status(500).send("Database Error: " + err.message);
    }
});

export default router;