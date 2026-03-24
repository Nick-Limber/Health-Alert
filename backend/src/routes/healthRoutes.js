import express from "express";
import { db_pool } from "../config/db.js";

const router = express.Router();

router.get("/pastdata", async (req, res) => {
    try {
        const profileId = req.query.profile_id;

        //weight
        const [weights] = await db_pool.query(
            `SELECT weight, recorded_at AS recordAT
             FROM personal_information
             WHERE profile_id = ?` , 
             [profileId]
        );

        //nutrition
        const [nutrition] = await db_pool.query(
            `SELECT recordAt, calories, protein, carbs 
             FROM diets
             WHERE profile_id = ?`,
             [profileId] 
        );

        //exercise
        const [exercise] = await db_pool.query(
            `SELECT recorded_at AS recordAT, exercise_type, sets, reps, weight
             FROM exercise
             WHERE profile_id = ?`, 
             [profileId]
        );

        res.json({
            weights,
            nutrition,
            exercise
        });
    }
    catch (err) {
        console.error(err);
        res.status(500).json({ message: "Database Error"});
    }

});

export default router;