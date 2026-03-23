package com.example.healthalert2

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
            Log.i("Test Credentials", "Email : $email and Password : $password")
            editor.putBoolean("isLoggedIn", true)
            editor.apply()

            Toast.makeText(this, "Logged in!", Toast.LENGTH_SHORT).show()
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