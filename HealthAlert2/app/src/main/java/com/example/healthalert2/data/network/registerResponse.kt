package com.example.healthalert2.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

    @JsonClass(generateAdapter = true)
    data class registerResponse(
        @Json(name = "status") val status: String,
        @Json(name = "token") val token: String,
        @Json(name = "id") val id: Int? = null,
        @Json(name = "user_id") val user_id: Int? = null,
        @Json(name = "userId") val userId: Int? = null
    )