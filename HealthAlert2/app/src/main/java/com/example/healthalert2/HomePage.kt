package com.example.healthalert2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Toast

class HomePage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setOnItemSelectedListener {
            when(it.itemId) {

                R.id.nav_home -> {
                    Toast.makeText(this, "Home Selected", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.nav_forum -> {
                    Toast.makeText(this, "Forum Selected", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.nav_account -> {
                    Toast.makeText(this, "Account Selected", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }
    }
}