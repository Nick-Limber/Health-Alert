package com.example.healthalert2.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RegisterApiService {

    @POST("/authentication/register")
    suspend fun registerUser(
        @Body registerRequest: registerRequest
    ) : Response<registerResponse>

}