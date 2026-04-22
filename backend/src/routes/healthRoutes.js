import express from "express";
import { db_pool } from "../config/db.js";

const router = express.Router();

router.get("/pastdata", async (req, res) => {
    try {
        const profileId = req.query.profile_id;
        console.log("--New REQUEST FOR PROFILE_ID:", profileId);

        if (!profileId) {
            return res.json({

                //sample data
                weights: [
                    { weight: 200, recorded_at: "2026-03-01 10:45:11" },
                    { weight: 190, recorded_at: "2026-03-02 11:30:33" },
                    { weight: 175, recorded_at: "2026-03-07 11:30:33" }
                ],

                nutrition: [
                    { recorded_at: "2026-03-01 09:13:15", calories: 200, protein: 15, carbohydrates: 30 },
                    { recorded_at: "2026-03-02 12:13:15", calories: 450, protein: 50, carbohydrates: 30 }
                ],

                exercise: [
                    { recorded_at: "2026-03-25 05:44:30", exercise_type: "Bench Press", sets: 4, reps: 10, weight: 225 },
                    { recorded_at: "2026-03-25 05:44:30", exercise_type: "Squat", sets: 5, reps: 5, weight: 250 }
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
        console.log("ERROR IN HEALTH ROUTE:");
        console.error(err); // This prints the full red error in the terminal
        res.status(500).send("Database Error: " + err.message);
    }

});

//new route for home page
router.post("/log-weight", async (req, res) => {
    try {
        const { profile_id, weight } = req.body;

        console.log(`-- NEW WEIGHT LOG REQUEST: Profile ${profile_id}, Weight: ${weight}`);

        //validation
        if (!profile_id || !weight) {
            return res.status(400).json({ 
                success: false,
                error: "Missing profile_id or weight value."
            });
        }

        const query = `
            INSERT INTO personal_information (profile_id, weight, recorded_at)
            VALUES (?, ?, NOW())
        `;

        const [result] = await db_pool.query(query, [profile_id, weight]);

        res.status(200).json({
            success: true,
            message: "Weight logged successfully.",
            id: result.insertId
        });
    } catch (err) {
        console.log("ERROR IN HEALTH POST ROUTE:");
        console.error(err);
        res.status(500).json({
            success: false,
            error: "Database error: " + err.message
        });
    }
});
 
export default router;