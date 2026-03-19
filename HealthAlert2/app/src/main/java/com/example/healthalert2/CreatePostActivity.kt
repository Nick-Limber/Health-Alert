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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        etTitle = findViewById(R.id.etTitle)
        etContent = findViewById(R.id.etContent)
        btnSubmit = findViewById(R.id.btnSubmitPost) // XML needs to match this

        btnSubmit.setOnClickListener {

            val title = etTitle.text.toString()
            val content = etContent.text.toString()

            val newPost = CreatePostRequest(
                userId = 1,
                title = title,
                content = content
            )

            RetrofitInstance.api.createPost(newPost)
                .enqueue(object : Callback<Map<String, Any>> {

                    override fun onResponse(
                        call: Call<Map<String, Any>>,
                        response: Response<Map<String, Any>>
                    ) {
                        if (response.isSuccessful) {

                            Toast.makeText(
                                this@CreatePostActivity,
                                "Post created!",
                                Toast.LENGTH_SHORT
                            ).show()

                            val resultIntent = Intent()
                            resultIntent.putExtra("title", title)
                            resultIntent.putExtra("content", content)

                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()

                        } else {
                            Toast.makeText(
                                this@CreatePostActivity,
                                "Error creating post",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                        Toast.makeText(
                            this@CreatePostActivity,
                            "Network error: ${t.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }
    }
}