import express from "express";
import { db_pool } from "../config/db.js";

const router = express.Router();

router.get("/pastdata", async (req, res) => {
    try {
        const profileId = 1;
        
        //weight
        console.log("Running weight...");
        const [weightRows] = await db_pool.query(
            `SELECT weight AS weight, recorded_at AS recordAt
            FROM personal_information 
            WHERE profile_id =?
            ORDER BY recorded_at ASC`, [profileId]
        );
        console.log("Weights:", weightRows);

        //nutrition
        console.log("Running nutrition...");
        const [nutritionRows] = await db_pool.query(
            `SELECT diet_name, calories, protein, carbohydrates AS carbs, recorded_at AS recordAt
            FROM diets
            WHERE profile_id = ?
            ORDER BY recorded_at ASC`, [profileId]
        );
        console.log("Nutrition: ", nutritionRows);

        //exercise
        console.log("Running exercise...");
        const [exerciseRows] = await db_pool.query(
            `SELECT exercise_type, sets, reps, weight recorded_at AS recordAt
            FROM exercise 
            WHERE profile_id = ?
            ORDER BY recorded_at`, [profileId]
        );
        console.log("Exercise: ", exerciseRows);


        res.json({
            weights: weightRows,
            nutrition: nutritionRows,
            exercise: exerciseRows
        });
    }
    catch (err) {
        console.error(err);
        res.status(500).json({ message: "Database Error"});
    }

});

export default router;