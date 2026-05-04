package com.example.healthalert2.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface DietApiService {

    @POST("/diet/addDiet")
    suspend fun addDiet(
        @Header("Authorization") token: String?,
        @Body request: AddDietRequest
    ) : Response<AddDietResponse>

    @GET("/diet/getDiets")
    suspend fun getDiets(
        @Header("Authorization") token: String?,
    ) : Response<getDietsResponse>

}