package com.example.healthalert2

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Toast
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.example.healthalert2.data.network.LoginRequest
import com.example.healthalert2.data.network.RetrofitClient
import kotlinx.coroutines.launch
import com.example.healthalert2.data.network.LoginApiService
import com.example.healthalert2.data.network.registerRequest


class RegisterPage : AppCompatActivity() {

    lateinit var emailInput: EditText
    lateinit var usernameInput: EditText
    lateinit var birthdayInput: EditText
    lateinit var passwordInput: EditText
    lateinit var registerBtn: Button

    lateinit var prefs: android.content.SharedPreferences
    lateinit var editor: android.content.SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_join_now)
        prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        editor = prefs.edit()
        // Login setup
        emailInput = findViewById(R.id.email_input)
        usernameInput = findViewById(R.id.username_input)
        birthdayInput = findViewById(R.id.birthday_input)
        passwordInput = findViewById(R.id.password_input)
        registerBtn = findViewById(R.id.register_btn)

        registerBtn.setOnClickListener {

            lifecycleScope.launch {
                try {
                    val email = emailInput.text.toString().trim()
                    val username = usernameInput.text.toString()
                    val birthday = birthdayInput.text.toString()
                    val pass = passwordInput.text.toString()

                    val response = RetrofitClient.registerApiService.registerUser(
                        registerRequest(username, email, birthday, pass)
                    )

                    if (response.isSuccessful) { val registerResponse = response.body()
                        val token = registerResponse?.token
                        if (token != null) {
                            Log.d("REGISTER_DEBUG", "Success! Saving token and switching.")

                            editor.putString("auth_token", token)
                            editor.apply()

                            Toast.makeText(this@RegisterPage, "Login Successful!", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@RegisterPage, HomePage::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Log.e("Register_DEBUG", "Register success, but token was null")
                        }
                    } else {
                        Log.e("Register_DEBUG", "Server error code: ${response.code()}")
                        Toast.makeText(this@RegisterPage, "Login Failed: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                } catch (t: Throwable) {
                    Log.e("LOGIN_DEBUG", "Network/Parsing Error: ${t.message}")
                    Toast.makeText(this@RegisterPage, "Connection Error", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}