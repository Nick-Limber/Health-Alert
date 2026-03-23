package com.example.healthalert2

import android.content.Context
import android.os.Bundle
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
import com.example.healthalert2.data.network.LoginRequest
import com.example.healthalert2.data.network.LoginResponse
import com.example.healthalert2.data.network.RetrofitClient
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback


class LoginPage : AppCompatActivity() {

    // Login views
    lateinit var emailInput: EditText
    lateinit var passwordInput: EditText
    lateinit var loginBtn: Button

    lateinit var prefs: android.content.SharedPreferences
    lateinit var editor: android.content.SharedPreferences.Editor
    // Bottom Navigation
    //private lateinit var bottomNavigationView: BottomNavigationView

    // Fragments
    //private val homeFragment = HomeFragment()
    //private val notificationFragment = NotificationFragment()
    //private val accountFragment = AccountFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_login_page) //changed this from activity_main to login_page fixed R.id.----- errors below
        prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        editor = prefs.edit()
        // Login setup
        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.login_btn)

        loginBtn.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            // 1. Create the request object (using the data class you made)
            val loginRequest = LoginRequest(email, password)

            // 2. Make the API call
            RetrofitClient.generatePlanApiService.loginApiService(loginRequest).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val authResponse = response.body()!!

                        // 3. Store the Token and Status
                        val sharedPref = getSharedPreferences("HealthPrefs", Context.MODE_PRIVATE)
                        val editor = sharedPref.edit()

                        editor.putString("jwt_token", authResponse.data.token)
                        editor.putBoolean("isLoggedIn", true)
                        editor.apply()

                        Toast.makeText(this@LoginActivity, "Welcome back!", Toast.LENGTH_SHORT).show()

                        // 4. Navigate to Main Screen
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Login Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("API_ERROR", "Message: ${t.message}")
                    Toast.makeText(this@LoginActivity, "Network Error!", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // Bottom Navigation setup

        // Window Insets
        //ViewCompat.setOnApplyWindowInsetsListener(
        //    findViewById<View>(R.id.main)
        //) { v, insets ->
        //    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        //    v.setPadding(
        //        systemBars.left,
        //        systemBars.top,
        //        systemBars.right,
        //        systemBars.bottom
         //   )
        //    insets
       // }
    }
}