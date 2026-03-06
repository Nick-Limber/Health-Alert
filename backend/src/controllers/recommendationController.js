import { db_pool } from "../config/db.js";
import { randomExercise } from "../utils/reccomendHelper.js"

const generate = async (req, res) => {

    const { height, weight, age, goal, muscle, level, access } = req.body;

    let bmi = (weight / (height ** 2)) * 703;
    const overweight = 25.0;
    const age_threshold = 40;
    const base_sql = "SELECT exercise_id, exercise_name, muscle_target, category FROM exercise_list WHERE ";
    let final_sql;


    // DAY LIST STRUCTURE [day number][exercise number][exercise object key]
    let day1_list = [];
    let day2_list = [];
    let used_ex = [];
    let muscle_tracker = [];
    let exercise_result;


    try {

        if (level === "beginner") {
            final_sql = base_sql + "level = beginner ";

            if (access === "bodyweight") {
                final_sql = final_sql + "AND equipment = body only";
            }

        }
        else {
            final_sql = base_sql + "(level = intermediate OR level = expert) ";

            if (access === "bodyweight") {
                final_sql = final_sql + "AND equipment = body only";
            }
        }

        const [result] = await db_pool.execute(final_sql);

        if (bmi > overweight || age > age_threshold) {

            const cardio = result.filter(row => row.category === 'cardio');
            exercise_result = randomExercise(cardio)
            day1_list.push(exercise_result);
            used_ex.push(exercise_result)

            exercise_result = randomExercise(cardio)
            day2_list.push(exercise_result);
            used_ex.push(exercise_result)

            //handle goal (lose weight or gain muscle)
            if (goal === "weight loss") {

                let avail_cardio = result.filter(row => row.category === 'cardio' && !used_id.includes(row.id));
                exercise_result = randomExercise(avail_cardio);
                day1_list.push(exercise_result);
                used_ex.push(exercise_result);

                avail_cardio = result.filter(row => row.category === 'cardio' && !used_id.includes(row.id));
                exercise_result = randomExercise(avail_cardio);
                day1_list.push(exercise_result);
                used_ex.push(exercise_result);
            }
            else {
                if (muscle != "None") {

                    muscle_tracker.push(muscle);

                    for (let ex_num = 0; ex_num <= 3; ex_num++) {

                        let primary_muscle = result.filter(row => row.muscle === muscle && row.category == stregth && !used_id.includes(row.id));

                        exercise_result = randomExercise(primary_muscle);
                        day1_list.push(exercise_result);
                        used_ex.push(exercise_result);
                    }
                    for (let ex_num = 0; ex_num <= 3; ex_num++) {

                        let secondary_muscles = result.filter(row => !muscle_tracker.includes(row.muscle) && row.category == stregth && !used_id.includes(row.id));

                    }
                }

            }
        }
        else {

        }

    }
}
