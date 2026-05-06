package com.example.healthalert2

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PostApi {
    // Sends request to backend for posts
    @GET("posts")
    fun getPosts(): Call<List<Post>>

    // Sends new post data as JSON to be inserted into MySQL
    @POST("posts")
    fun createPost(@Body post: CreatePostRequest): Call<Map<String, Any>>

    // To edit post
    @PUT("posts/{id}")
    fun updatePost(
        @Path("id") id: Int,
        @Body post: Post
    ): Call<Void>

    // To delete post
    @DELETE("posts/{id}")
    fun deletePost(@Path("id") postId: Int): Call<Void>

    // To send a reply to a post
    @POST("posts/{postId}/replies")
    fun sendReply(
        @Path("postId") postId: Int,
        @Body request: CreateReplyRequest
    ): Call<Void>

    // To fetch all replies for a specific post
    @GET("posts/{postId}/replies")
    fun getReplies(@Path("postId") postId: Int): Call<List<Reply>>

    @DELETE("posts/{postId}/replies/{id}")
    fun deleteReply(
        @Path("postId") postId: Int,
        @Path("id") replyId: Int
    ): Call<Void>
}
