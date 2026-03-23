package com.example.healthalert2.data.network


import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("upgrade")
    fun upgradeUser(@Body body: Map<String, Int>): Call<ResponseBody>
}