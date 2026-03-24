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

//activity for creating or editing a post
//temporary userID (authentication not connected)
class CreatePostActivity : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText
    private lateinit var btnSubmit: Button

    //tracks if editing an existing post
    private var postId: Int = -1
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //connect to XML layout
        setContentView(R.layout.activity_create_post)

        //link UI components
        etTitle = findViewById(R.id.etTitle)
        etContent = findViewById(R.id.etContent)
        btnSubmit = findViewById(R.id.btnSubmitPost)

        //check if editing an existing post
        postId = intent.getIntExtra("postId", -1)

        if (postId != -1) {
            isEditMode = true
            //get existing data from previous activity
            val title = intent.getStringExtra("title")
            val content = intent.getStringExtra("content")

            //pre-fill input fields
            etTitle.setText(title)
            etContent.setText(content)
            btnSubmit.text = "Update Post"
        }

        btnSubmit.setOnClickListener {
            val title = etTitle.text.toString()
            val content = etContent.text.toString()
//validate input
            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isEditMode) {
                // Edit mode -> UPDATE EXISITING POST
                val updatedPost = Post(postId, 1, title, content, "")
                RetrofitInstance.api.updatePost(postId, updatedPost).enqueue(object : Callback<Void> {
                    // if backend responds
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            // show success + go back
                            Toast.makeText(this@CreatePostActivity, "Post updated", Toast.LENGTH_SHORT).show()
                            setResult(Activity.RESULT_OK)
                            finish()
                        } else {
                            Toast.makeText(this@CreatePostActivity, "Update failed", Toast.LENGTH_SHORT).show()
                        }
                    }
// if request fails
                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@CreatePostActivity, "Failed to update", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                // CREATE mode -> CREATE NEW POST
                val newPost = CreatePostRequest(
                    userId = 1, //hardcoded temp user for now
                    title = title,
                    content = content
                )
                RetrofitInstance.api.createPost(newPost).enqueue(object : Callback<Map<String, Any>> {
                    override fun onResponse(
                        call: Call<Map<String, Any>>,
                        response: Response<Map<String, Any>>
                    ) {
                        if (response.isSuccessful) {
                            //show success and go back
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
