import { db_pool } from "../config/db.js";
import { randomExercise } from "../utils/reccomendHelper.js"

const addDiet = async (req, res) => {

    try {

        const { diet_name, calories, protein, carbohydrates } = req.body;
        const profile_id = req.user;

        const sql = "INSERT INTO diets (diet_name, calories, protein, carbohydrates, profile_id) VALUES (?, ?, ?, ?, ?)";

        db_pool.execute(sql, [diet_name, calories, protein, carbohydrates, profile_id]);

        res.status(201).json({ success: "True" });

    } catch (error) {
        res.status(500).json({ message: "Unable to save diet to database.", error: `${error}` });
    }
}

const getDiets = async (req, res) => {

    try {
        const profile_id = req.user;

        const sql = "SELECT SUM(calories) AS total_calories, SUM(protein) AS total_protein, SUM(carbohydrates) AS total_carbs FROM (SELECT calories, protein, carbohydrates FROM diets WHERE profile_id = ? ORDER BY recorded_at DESC LIMIT 5) AS recent_entries";
        const [result] = db_pool.execute(sql, profile_id);

        const total_calories = result.total_calories;
        const total_protein = result.total_protein;
        const total_carbs = result.total_carbs;

        res.status(201).json({ success: "True" })

    } catch (error) {
        res.status(500).json({ message: "Unable to save diet to database.", error: `${error}` });
    }


}

export { addDiet, getDiets };