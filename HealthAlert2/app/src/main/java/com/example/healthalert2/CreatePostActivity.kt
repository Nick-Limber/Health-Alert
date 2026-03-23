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

class CreatePostActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText
    private lateinit var btnSubmit: Button

    private var postId: Int = -1
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        etTitle = findViewById(R.id.etTitle)
        etContent = findViewById(R.id.etContent)
        btnSubmit = findViewById(R.id.btnSubmitPost)

        // Check if editing
        postId = intent.getIntExtra("postId", -1)

        if (postId != -1) {
            isEditMode = true
            val title = intent.getStringExtra("title")
            val content = intent.getStringExtra("content")

            etTitle.setText(title)
            etContent.setText(content)
            btnSubmit.text = "Update Post"
        }

        btnSubmit.setOnClickListener {
            val title = etTitle.text.toString()
            val content = etContent.text.toString()

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isEditMode) {
                // UPDATE POST
                val updatedPost = Post(postId, 1, title, content, "")
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
                        Toast.makeText(this@CreatePostActivity, "Failed to update", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                // CREATE POST
                val newPost = CreatePostRequest(
                    userId = 1,
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
                        Toast.makeText(this@CreatePostActivity, "Failed to create post", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
}
