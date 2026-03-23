package com.example.healthalert2.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query
interface GeneratePlanApiService {

    @POST("/recommendation/generate")
    suspend fun generateWorkoutPlan(
        @Body request: LoginRequest
    ): Response<LoginResponse>

}