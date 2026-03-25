package com.example.healthalert2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
                   true
                }

                R.id.nav_forum -> {
                    val intent = Intent(this, CommunityForumActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_account -> {
                    val intent = Intent(this, AccountPage::class.java)
                    startActivity(intent)
                    true
                }

                R.id.workout_plan ->{
                    val intent = Intent(this, WorkoutPlanActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }
}