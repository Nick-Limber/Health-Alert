package com.example.healthalert2.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class LoginRequest(

    val email: String,
    val password: String

)
data class LoginResponse(
    val status: String,
    val data: LoginData
)

data class LoginData(
    val user: User,
    val token: String
)

data class User(
    val id: Int,
    val email: String
)