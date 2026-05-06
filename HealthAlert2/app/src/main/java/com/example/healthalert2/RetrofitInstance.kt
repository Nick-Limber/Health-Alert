package com.example.healthalert2

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//creates single global instance of the API (so whole app uses same connection)
object RetrofitInstance {
    private const val BASE_URL = "https://gleaming-sparkle-production-acb6.up.railway.app/"
    val api: PostApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PostApi::class.java)
    }
}