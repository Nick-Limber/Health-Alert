package com.example.healthalert2.data.repository

import com.example.healthalert2.data.network.ApiService
import com.example.healthalert2.data.network.GeneratePlanRequest
import com.example.healthalert2.data.network.GeneratePlanResponse
import retrofit2.Response

class WorkoutRepository(private val apiService: ApiService) {
    suspend fun getWorkoutPlan(request: GeneratePlanRequest): Response<GeneratePlanResponse> {
        return apiService.generateWorkoutPlan(request)
    }
}