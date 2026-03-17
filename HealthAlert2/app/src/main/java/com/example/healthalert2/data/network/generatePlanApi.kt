package com.example.healthalert2.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
interface ApiService {

    @POST("/recommendation/generate")
    suspend fun generateWorkoutPlan(
        @Body request: GeneratePlanRequest
    ): Response<GeneratePlanResponse>
}