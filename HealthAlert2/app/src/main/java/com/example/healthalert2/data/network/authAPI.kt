package com.example.healthalert2.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query

interface loginApiService {

    @POST("/authentication/login")
    suspend fun postAuthLogin(
        @Body request: GeneratePlanRequest
    ): Response<WorkoutResponse>


}