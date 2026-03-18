package com.example.healthalert2


//The data classes allow Retrofit to map JSON responses


//For retrieving from mysql
data class Post(
    val postId: Int,
    val userId: Int,
    val title: String,
    val content: String,
    val timestamp: String
)

//For creating posts and inserting into sql
//Don't have postId yet, timestamp created in Mysql
data class CreatePostRequest(
    val userId: Int,
    val title: String,
    val content: String
)