package com.example.healthalert2.data.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface ApiService {
    @POST("membership/toggle")
    fun toggleMembership(@Body body: Map<String, Int>): Call<MembershipResponse>

    @GET("membership/{id}")
    fun getMembership(@Path("id") userId: Int): Call<MembershipResponse>
}
