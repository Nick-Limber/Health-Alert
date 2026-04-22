package com.example.healthalert2.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddDietReponse(
    @Json(name = "success") val status: String,
)