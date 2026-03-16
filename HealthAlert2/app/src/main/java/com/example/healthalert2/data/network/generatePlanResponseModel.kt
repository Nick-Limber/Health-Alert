package com.example.healthalert2.data.network

import com.squareup.moshi.Json
data class GeneratePlanResponse(
    val success: Boolean,
    val data: WorkoutData
)
data class WorkoutData(
    val plan: PlanInfo,
    val days: List<WorkoutDay>
)
data class PlanInfo(
    val goal: String,
    val level: String
)
data class WorkoutDay(
    val day_number: Int, // Match the JSON exactly
    val exercises: List<Exercise>
)
data class Exercise(
    @Json(name = "exercise_id")
    val exerciseId: Int?,
    @Json(name = "exercise_name")
    val exerciseName: String?,
    @Json(name = "muscle_target")
    val muscleTarget: String?,
    val category: String?
)