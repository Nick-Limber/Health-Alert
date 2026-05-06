package com.example.healthalert2

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// The data classes allow Retrofit to map JSON responses

// For retrieving from mysql
@JsonClass(generateAdapter = true)
data class Post(
    val postId: Int,
    val title: String,
    val content: String,
    @Json( name = "profile_id") val profileID: Int = 0,      // Match the 'p.profileID' from SQL
    val username: String?,    // Match the 'pr.username' from SQL
    val timestamp: String?,
    val replies: List<Reply>? = emptyList()
)

// For creating posts and inserting into sql
data class CreatePostRequest(
    val profile_id: Int,
    val title: String,
    val content: String
)

// Reply model
data class Reply(
    val id: Int,
    val postId: Int,
    val profile_id: Int,
    val username: String?, // Added to match your backend JOIN
    val content: String,
    val timestamp: String?
)


//data class CreateReplyRequest(
  //  val profile_id: Int,
    //val content: String
//)
data class CreateReplyRequest(
    val postId: Int,
    val profile_id: Int,
    val username: String,
    val content: String
)