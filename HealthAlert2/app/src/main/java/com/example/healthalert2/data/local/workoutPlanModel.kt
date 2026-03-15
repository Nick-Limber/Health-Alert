import com.squareup.moshi.Json

data class WorkoutResponse(
    @Json(name = "workout_name") val workoutName: String,
    @Json(name = "goal") val goal: String,
    val days: List<WorkoutDay>
)

data class WorkoutDay(
    @Json(name = "day_number") val dayNumber: Int,
    val exercises: List<Exercise>
)

data class Exercise(
    val name: String,
    val target: String,
    val order: Int
)