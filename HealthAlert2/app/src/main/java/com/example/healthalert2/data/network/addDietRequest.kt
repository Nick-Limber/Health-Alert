package com.example.healthalert2.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddDietRequest(
    @Json(name = "diet_name") val email: String,
    @Json(name = "calories") val dob: Int,
    @Json(name = "protein") val password: Int,
    @Json(name = "carbohydrates") val carbohydrates: Int
)