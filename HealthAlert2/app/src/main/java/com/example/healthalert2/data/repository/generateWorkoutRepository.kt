package com.example.healthalert2.data.repository

import com.example.healthalert2.data.network.GeneratePlanApiService
import com.example.healthalert2.data.network.GeneratePlanRequest
import com.example.healthalert2.data.network.WorkoutResponse
import retrofit2.Response

class GenerateWorkoutRepository(private val apiService: GeneratePlanApiService) {

    suspend fun getPlans(token: String): Response<WorkoutResponse> {
        return apiService.fetchWorkoutPlan(token)
    }

    suspend fun getWorkoutPlan(token: String, request: GeneratePlanRequest): Response<WorkoutResponse> {
        return apiService.generateWorkoutPlan(token, request)
    }
}