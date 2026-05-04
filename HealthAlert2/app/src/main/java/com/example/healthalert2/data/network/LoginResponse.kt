package com.example.healthalert2.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@com.squareup.moshi.JsonClass(generateAdapter = true)
data class LoginResponse(
    val status: String,
    val data: LoginData
)

data class LoginData (
    val user : User,
    val token : String
)

data class User (
    val id : Int,
    val email : String,
    val username: String
)