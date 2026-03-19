package com.example.healthalert2.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WorkoutResponse(
    val success: Boolean,
    val data: List<WorkoutPlan>
)

@JsonClass(generateAdapter = true)
data class WorkoutPlan(
    @Json(name = "plan_id") val planId: Int,
    @Json(name = "workout_name") val workoutName: String,
    val goal: String,
    val days: List<WorkoutDay> // Nested list of days
)

@JsonClass(generateAdapter = true)
data class WorkoutDay(
    @Json(name = "day_number") val dayNumber: Int,
    val exercises: List<WorkoutExercise>
)

@JsonClass(generateAdapter = true)
data class WorkoutExercise(
    val name: String,
    val target: String,
    val order: Int
)