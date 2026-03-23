package com.example.healthalert2

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PostApi {
    //Sends request to backend for posts
    @GET("posts")
    fun getPosts(): Call<List<Post>>

    //sends new post data as JSON to be inserted into MySQL
    @POST("posts")
    fun createPost(@Body post: CreatePostRequest): Call<Map<String, Any>>

    //to edit post
    @PUT("posts/{id}")
    fun updatePost(
        @Path("id") id: Int,
        @Body post: Post
    ): Call<Void>
}