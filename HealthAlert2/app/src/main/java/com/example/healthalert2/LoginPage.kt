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

            lifecycleScope.launch {
                try {
                    val email = emailInput.text.toString().trim() // Added trim() to avoid accidental spaces
                    val pass = passwordInput.text.toString()

                    val response = RetrofitClient.loginApiService.loginUser(
                        LoginRequest(email, pass)
                    )

                    if (response.isSuccessful) { val loginResponse = response.body()
                        val token = loginResponse?.data?.token
                        if (token != null) {
                            Log.d("LOGIN_DEBUG", "Success! Saving token and switching.")

                            editor.putString("auth_token", token)
                            editor.apply()

                            Toast.makeText(this@LoginPage, "Login Successful!", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@LoginPage, HomePage::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Log.e("LOGIN_DEBUG", "Login success, but token was null")
                        }
                    } else {
                        Log.e("LOGIN_DEBUG", "Server error code: ${response.code()}")
                        Toast.makeText(this@LoginPage, "Login Failed: ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                } catch (t: Throwable) {
                    Log.e("LOGIN_DEBUG", "Network/Parsing Error: ${t.message}")
                    Toast.makeText(this@LoginPage, "Connection Error", Toast.LENGTH_SHORT).show()
                }
            }
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