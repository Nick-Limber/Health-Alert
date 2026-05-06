package com.example.healthalert2.data.network

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.Call

data class ExerciseRequest(
    val profile_id: Int,
    val exercise_type: String,
    val sets: Int,
    val reps: Int,
    val weight: Int
)

data class WeightRequest(
    val profile_id: Int,
    val weight: Int
)

data class AllHistoryResponse(
    val weights: List<WeightEntry>,
    val nutrition: List<NutritionEntry>,
    val exercise: List<ExerciseEntry>
)

data class WeightEntry (
    val weight: Double,
    val recorded_at: String
)

data class NutritionEntry (
    val diet_name: String,
    val calories: Int,
    val protein: Int,
    val carbohydrates: Int,
    val recorded_at: String
)

data class ExerciseEntry (
    val exercise_type: String,
    val sets: Int,
    val reps: Int,
    val weight: Int,
    val recorded_at: String
)

interface HealthApiService {
    @POST("health/log-exercise")
    suspend fun logExercise(
        @Header("Authorization") authHeader: String,
        @Body request: ExerciseRequest
    ): Response<Unit>

    @POST("health/log-weight")
    suspend fun logWeight(
        @Header("Authorization") authHeader: String,
        @Body request: WeightRequest
    ): Response<Unit>

    @GET("health/all-history")
    fun getAllHistory(
        @Header("Authorization") authHeader: String
    ): Call<AllHistoryResponse>
}