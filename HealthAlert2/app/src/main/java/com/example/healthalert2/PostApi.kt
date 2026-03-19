package com.example.healthalert2

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.POST

interface PostApi {
    //Sends request to backend for posts
    @GET("posts")
    fun getPosts(): Call<List<Post>>

    //sends new post data as JSON to be inserted into MySQL
    @POST("posts")
    fun createPost(@Body post: CreatePostRequest): Call<Map<String, Any>>
}