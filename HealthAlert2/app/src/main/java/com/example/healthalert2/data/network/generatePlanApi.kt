package com.example.healthalert2.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
interface GeneratePlanApiService {

    @POST("/recommendation/generate")
    suspend fun generateWorkoutPlan(
        @Header("Authorization") token: String,
        @Body request: GeneratePlanRequest
    ): Response<WorkoutResponse>

    @GET("recommendation/getPlans/")
    suspend fun fetchWorkoutPlan(
        @Header("Authorization") token: String
    ): Response<WorkoutResponse>

}