package com.example.healthalert2

// The data classes allow Retrofit to map JSON responses

// For retrieving from mysql
data class Post(
    val postId: Int,
    val title: String,
    val content: String,
    val profileID: Int = 0,      // Match the 'p.profileID' from SQL
    val username: String?,    // Match the 'pr.username' from SQL
    val timestamp: String?,
    val replies: List<Reply>? = emptyList()
)

// For creating posts and inserting into sql
data class CreatePostRequest(
    val userId: Int,
    val title: String,
    val content: String
)

// Reply model
data class Reply(
    val id: Int,
    val postId: Int,
    val userId: Int,
    val username: String?, // Added to match your backend JOIN
    val content: String,
    val timestamp: String?
)

data class CreateReplyRequest(
    val userId: Int,
    val content: String
)