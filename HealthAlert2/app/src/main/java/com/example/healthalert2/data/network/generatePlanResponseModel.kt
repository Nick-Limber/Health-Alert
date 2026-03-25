package com.example.healthalert2.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WorkoutResponse(
    val success: Boolean,
    val data: List<WorkoutData>
)

@JsonClass(generateAdapter = true)
data class WorkoutData(
    @Json(name = "plan_id") val planId: Int,
    @Json(name = "workout_name") val workoutName: String?,
    val goal: String,
    val days: List<WorkoutDay>
)

@JsonClass(generateAdapter = true)
data class PlanInfo(

    @Json(name = "workout_name") val workoutName: String?,
    val goal: String

)

@JsonClass(generateAdapter = true)
data class WorkoutDay(
    @Json(name = "day_number") val dayNumber: Int,
    val exercises: List<WorkoutExercise>
)

@JsonClass(generateAdapter = true)
data class WorkoutExercise(
    @Json(name = "exercise_name") val exerciseName: String, // Fixes 'exercise_name'
    @Json(name = "muscle_target") val muscleTarget: String, // Fixes 'muscle_target'
    val category: String                                   // Fixes 'category'
)