package com.example.healthalert2.data.network
data class GeneratePlanRequest(
    val height: Int,
    val weight: Int,
    val age: Int,
    val goal: String,
    val muscle: String,
    val level: String,
    val access: String?,
    val workout_name: String?
)