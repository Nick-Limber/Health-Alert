import express from "express";
import { db_pool } from "../config/db.js";

const router = express.Router();

router.get("/pastdata", async (req, res) => {
    try {
        const profileId = req.query.profile_id;

        if(!profileId)
        {
            return res.json({

                //sample data
                weights : [
                    { weight: 200, recorded_at: "2026-03-01 10:45:11"},
                    { weight: 190, recorded_at: "2026-03-02 11:30:33"},
                    { weight: 175, recorded_at: "2026-03-07 11:30:33"}
                ],

                nutrition : [
                    { recorded_at: "2026-03-01 09:13:15", calories: 200, protein: 15, carbohydrates: 30},
                    { recorded_at: "2026-03-02 12:13:15", calories: 450, protein: 50, carbohydrates: 30}
                ],

                exercise : [
                    { recorded_at: "2026-03-25 05:44:30", exercise_type: "Bench Press", sets: 4, reps: 10, weight: 225},
                    { recorded_at: "2026-03-25 05:44:30", exercise_type: "Squat", sets: 5, reps: 5, weight: 250}
                ]
            })
        }

        //weight
        const [weights] = await db_pool.query(
            `SELECT weight, recorded_at
             FROM personal_information
             WHERE profile_id = ?` , 
             [profileId]
        );

        //nutrition
        const [nutrition] = await db_pool.query(
            `SELECT recorded_at, diet_name, calories, protein, carbohydrates 
             FROM diets
             WHERE profile_id = ?`,
             [profileId] 
        );

        //exercise
        const [exercise] = await db_pool.query(
            `SELECT recorded_at, exercise_type, sets, reps, weight
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