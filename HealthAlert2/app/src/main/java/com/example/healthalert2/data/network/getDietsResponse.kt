package com.example.healthalert2.data.network


@com.squareup.moshi.JsonClass(generateAdapter = true)
data class getDietsResponse(
    val success: String,
    val calories: Int,
    val protein: Int,
    val carbs: Int
)
