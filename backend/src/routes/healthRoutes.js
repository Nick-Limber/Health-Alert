import express from "express";
import { db_pool } from "../config/db.js";
import { verificationMiddleware } from "../middleware/verificationMiddleware.js";

const router = express.Router();
console.log(" HEALTH ROUTES HAVE SUCCESSFULLY LOADED INTO MEMORY ");

router.get("/all-history/", verificationMiddleware, async (req, res) => {
    try {
        const  profile_id  = req.user;
        console.log("--New REQUEST FOR PROFILE_ID:", profile_id);
        
        //weight
        const [weights] = await db_pool.query(
            `SELECT weight, recorded_at
             FROM personal_information
             WHERE profile_id = ?
             ORDER BY recorded_at DESC`,
            [profile_id]
        );
        //nutrition
        const [nutrition] = await db_pool.query(
            `SELECT recorded_at, diet_name, calories, protein, carbohydrates 
             FROM diets
             WHERE profile_id = ?
             ORDER BY recorded_at DESC`,
            [profile_id]
        );
        //exercise
        const [exercise] = await db_pool.query(
            `SELECT recorded_at, exercise_type, sets, reps, weight
             FROM exercise
             WHERE profile_id = ?
             ORDER BY recorded_at DESC`,
            [profile_id]
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
router.post("/log-weight", verificationMiddleware,async (req, res) => {
    try {
        const profile_id = req.user;
        const { weight } = req.body;

        console.log(`-- NEW WEIGHT LOG REQUEST: Profile ${profile_id}, Weight: ${weight}`);

    if (weight === undefined || weight === null) {
        return res.status(400).json({
            error: "Weight value is required."
        });
    }

        const query = `
            INSERT INTO personal_information (profile_id, weight, height, recorded_at)
            VALUES (?, ?, 0, NOW())
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

//new route for home page
router.post("/log-exercise", verificationMiddleware,async (req, res) => {
    try {
        const profile_id = req.user; 
        const { exercise_type, sets, reps, weight } = req.body;

        console.log(`-- NEW EXERCISE LOG REQUEST: Profile ${profile_id}, Exercise: ${exercise_type}, Sets: ${sets}, Reps: ${reps}, Weight: ${weight}`);

        //validation
        if (!profile_id || !exercise_type || !sets || !reps || weight === undefined) {
            return res.status(400).json({ 
                success: false,
                error: "Missing required fields. Please provide profile_id, exercise_type, sets, reps, and weight."
            });
        }

        const query = `
            INSERT INTO exercise (profile_id, exercise_type, sets, reps, weight, recorded_at)
            VALUES (?, ?, ?, ?, ?, NOW())
        `;

        const [result] = await db_pool.query(query, [profile_id, exercise_type, sets, reps, weight]);

        res.status(200).json({
            success: true,
            message: "Exercise logged successfully.",
            id: result.insertId
        });
    } catch (err) {
        console.log("ERROR IN EXERCISE POST ROUTE:");
        console.error(err);
        res.status(500).json({
            success: false,
            error: "Database error: " + err.message
        });
    }
});
 
export default router;