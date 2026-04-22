package com.example.healthalert2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.healthalert2.data.network.RetrofitClient
import com.example.healthalert2.data.network.AddDietRequest // Ensure this matches your actual data class name
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class HomePage : AppCompatActivity() {

    // 1. Move views here so they are accessible to all functions in the class
    private lateinit var inputName: EditText
    private lateinit var inputCalories: EditText
    private lateinit var inputProtein: EditText
    private lateinit var inputCarbs: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        // 2. Initialize the views
        inputName = findViewById(R.id.inputName)
        inputCalories = findViewById(R.id.inputCalories)
        inputProtein = findViewById(R.id.inputProtein)
        inputCarbs = findViewById(R.id.inputCarbs)
        val logMealBtn = findViewById<Button>(R.id.logMealBtn)

        logMealBtn.setOnClickListener {
            val name = inputName.text.toString().trim()
            val calories = inputCalories.text.toString().trim().toIntOrNull()
            val protein = inputProtein.text.toString().trim().toIntOrNull()
            val carbs = inputCarbs.text.toString().trim().toIntOrNull()

            if (name.isNotEmpty() && calories != null && protein != null && carbs != null) {
                saveMealToDatabase(name, calories, protein, carbs)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                if (name.isEmpty()) inputName.error = "Required"
                if (calories == null) inputCalories.error = "Invalid number"
                if (protein == null) inputProtein.error = "Invalid number"
                if (carbs == null) inputCarbs.error = "Invalid number"
            }
        }

        // Bottom Navigation Logic
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.nav_home -> true
                R.id.nav_forum -> {
                    startActivity(Intent(this, CommunityForumActivity::class.java))
                    true
                }
                R.id.nav_account -> {
                    startActivity(Intent(this, AccountPage::class.java))
                    true
                }
                R.id.workout_plan -> {
                    startActivity(Intent(this, WorkoutPlanActivity::class.java))
                    true
                }
                R.id.nav_past_data -> {
                    startActivity(Intent(this, ViewPastDataActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    // 3. This function is now part of the class and can see the lateinit variables
    private fun saveMealToDatabase(name: String, calories: Int, protein: Int, carbs: Int) {
        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val token = sharedPref.getString("jwt_token", "") ?: ""
        val authHeader = "Bearer $token"

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.addDietApiService.AddDiet(
                    authHeader,
                    AddDietRequest(name, calories, protein, carbs)
                )

                if (response.isSuccessful) {
                    Toast.makeText(this@HomePage, "Meal Added Successfully!", Toast.LENGTH_SHORT).show()

                    inputName.text.clear()
                    inputCalories.text.clear()
                    inputProtein.text.clear()
                    inputCarbs.text.clear()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Toast.makeText(this@HomePage, "Error: $errorBody", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@HomePage, "Failure: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }
}