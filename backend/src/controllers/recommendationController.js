import { db_pool } from "../config/db.js";
import { randomExercise } from "../utils/reccomendHelper.js"

const generate = async (req, res) => {

    const { profile_id, height, weight, age, goal, muscle, level, access, workout_name } = req.body;

    // CHECK HOW MANY ACTIVE RECCOMENDATIONS USER HAS
    const active_sql = "SELECT * FROM workout_plans WHERE profile_id = ? AND active = ?";
    const [rows] = await db_pool.execute(active_sql, [profile_id, true]);

    if (rows.length < 2) {

        let bmi = (weight / (height ** 2)) * 703;
        const bmi_threshold = 25.0;
        const age_threshold = 40;
        const base_sql = "SELECT exercise_id, exercise_name, muscle_target, category FROM exercise_list WHERE ";
        let final_sql;


        // DAY LIST STRUCTURE [day number][exercise number][exercise object key]
        let day1_list = [];
        let day2_list = [];


        try {

            if (level === "beginner") {
                final_sql = base_sql + "exercise_level = 'beginner' ";
            }
            else {
                final_sql = base_sql + "(exercise_level = 'intermediate' OR exercise_level = 'expert') ";
            }
            if (access === "bodyweight") {
                final_sql = final_sql + "AND equipment = 'body only' ";
            }

            const [result] = await db_pool.execute(final_sql);

            if (bmi > bmi_threshold || age > age_threshold) {
                day1_list.push("cardio");
                day2_list.push("cardio");
            }
            if (goal === "lose weight") {
                day1_list.push("cardio");
                day2_list.push("cardio");
            }

            let day1_len = day1_list.length;
            if (muscle != "None") {
                for (let i = day1_len; i < 5; i++) {
                    day1_list.push(muscle);
                }
            }

            while (day1_list.length < 5) day1_list.push('any_strength');
            while (day2_list.length < 5) day2_list.push('any_strength');

            const usedIds = new Set();

            const buildDay = (slots, pool) => {
                // Return the result of the map!
                return slots.map(slot => {
                    let candidates;
                    if (slot === 'cardio') {
                        candidates = pool.filter(row => row.category === 'cardio' && !usedIds.has(row.exercise_id));
                    } else if (slot === 'any_strength') {
                        candidates = pool.filter(row => row.category === 'strength' && row.muscle_target !== muscle && !usedIds.has(row.exercise_id));
                    } else {
                        candidates = pool.filter(row => row.muscle_target === slot && !usedIds.has(row.exercise_id));
                    }

                    // Safety fallback: if candidates is empty, use the whole pool
                    const selection = randomExercise(candidates.length ? candidates : pool);

                    if (selection) {
                        usedIds.add(selection.exercise_id);
                        return selection;
                    }
                    return { exercise_name: "Generic Exercise", exercise_id: 0 }; // Ultimate fallback
                });
            };

            const day1_plan = buildDay(day1_list, result);
            const day2_plan = buildDay(day2_list, result);

            // HANDLE DATA INSERTION INTO THE MULTIPLE TABLES

            const connection = await db_pool.getConnection();

            try {
                await connection.beginTransaction();

                const plan_insert = "INSERT INTO workout_plans (goal, target, active, profile_id, workout_name) VALUES (?, ?, ?, ?, ?)";
                const [planRows] = await connection.execute(plan_insert, [goal, muscle, 1, profile_id, workout_name]);
                const plan_id = planRows.insertId;

                const final_days = [day1_plan, day2_plan];

                for (let i = 0; i < final_days.length; i++) {

                    const [day_rows] = await connection.execute("INSERT INTO workout_days (day_number, plan_id) VALUES (?, ?)", [i + 1, plan_id]);
                    const day_id = day_rows.insertId;

                    const exerciseValues = final_days[i].map((exercise, index) => [
                        day_id,
                        exercise.exercise_id,
                        index + 1,
                    ]);

                    await connection.query(
                        "INSERT INTO day_exercises (day_id, exercise_id, exercise_order) VALUES ?",
                        [exerciseValues]
                    );
                }

                await connection.commit();

                return res.status(201).json({
                    success: true,
                    data: {
                        plan: {
                            goal: goal,
                            level: level,
                        },
                        days: [
                            {
                                day_number: 1,
                                exercises: day1_plan
                            },
                            {
                                day_number: 2,
                                exercises: day2_plan
                            }
                        ]
                    }
                });


            } catch (error) {
                await connection.rollback();
                res.status(500).json({ message: "Unable to save workout plan to database.", error: `${error}` });
            } finally {
                connection.release();
            }

        }
        catch (error) {
            return res.status(500).json({ message: `${error}` });
        }
    }
    else {
        return res.status(401).json({ error: "Too many active reccomendations" });
    }
}

export { generate };


