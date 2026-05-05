package com.example.healthalert2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// activity for creating or editing a post
class CreatePostActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText
    private lateinit var btnSubmit: Button

    private var postId: Int = -1
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val currentUserId = sharedPreferences.getInt("profile_id", -1000)

        etTitle = findViewById(R.id.etTitle)
        etContent = findViewById(R.id.etContent)
        btnSubmit = findViewById(R.id.btnSubmitPost)

        postId = intent.getIntExtra("postId", -1)

        if (postId != -1) {
            isEditMode = true
            etTitle.setText(intent.getStringExtra("title"))
            etContent.setText(intent.getStringExtra("content"))
            btnSubmit.text = "Update Post"
        }

        btnSubmit.setOnClickListener {

            val title = etTitle.text.toString()
            val content = etContent.text.toString()

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isEditMode) {
                // UPDATE POST
                // Fix: We use named arguments to ensure the values match the correct fields
                val updatedPost = Post(
                    postId = postId,
                    title = title,
                    content = content,
                    profileID = currentUserId,
                    username = "",
                    timestamp = "",
                    replies = emptyList()
                )

                RetrofitInstance.api.updatePost(postId, updatedPost).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@CreatePostActivity, "Post updated", Toast.LENGTH_SHORT).show()
                            setResult(Activity.RESULT_OK)
                            finish()
                        } else {
                            Toast.makeText(this@CreatePostActivity, "Update failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@CreatePostActivity, "Network error", Toast.LENGTH_SHORT).show()
                    }
                })

            } else {
                // CREATE POST
                val newPost = CreatePostRequest(
                    profile_id = currentUserId,
                    title = title,
                    content = content
                )

                RetrofitInstance.api.createPost(newPost).enqueue(object : Callback<Map<String, Any>> {
                    override fun onResponse(
                        call: Call<Map<String, Any>>,
                        response: Response<Map<String, Any>>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@CreatePostActivity, "Post created", Toast.LENGTH_SHORT).show()
                            setResult(Activity.RESULT_OK)
                            finish()
                        } else {
                            Toast.makeText(this@CreatePostActivity, "Creation failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                        Toast.makeText(this@CreatePostActivity, "Network error", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

    }

}
