package com.example.healthalert2.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AddDietApiService {

    @POST("/diet/addDiet")
    suspend fun AddDiet(
        @Header("Authorization") token: String,
        @Body addDietRequest: AddDietRequest
    ) : Response<AddDietReponse>

}