package com.example.healthalert2.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

    @JsonClass(generateAdapter = true)
    data class registerRequest(
        @Json(name = "username") val username: String,
        @Json(name = "email") val email: String,
        @Json(name = "d_o_b") val dob: String,
        @Json(name = "password") val password: String
    )