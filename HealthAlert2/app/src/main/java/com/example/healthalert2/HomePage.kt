package com.example.healthalert2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.healthalert2.data.network.AddDietRequest
import com.example.healthalert2.data.network.RetrofitClient
import com.example.healthalert2.data.network.getDietsResponse
import com.example.healthalert2.databinding.ActivityHomePageBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class HomePage : AppCompatActivity() {

    // Class-level properties (accessible to all functions)
    private lateinit var inputName: EditText
    private lateinit var inputCalories: EditText
    private lateinit var inputProtein: EditText
    private lateinit var inputCarbs: EditText
    lateinit var prefs: android.content.SharedPreferences

    private lateinit var binding: ActivityHomePageBinding
    private val dietService = RetrofitClient.dietApiService



    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        // 1. Initialize the binding FIRST
        binding = ActivityHomePageBinding.inflate(layoutInflater)

        // 2. Set the content view using the binding root
        setContentView(binding.root)

        // 1. Initialize Views
        inputName = findViewById(R.id.inputName)
        inputCalories = findViewById(R.id.inputCalories)
        inputProtein = findViewById(R.id.inputProtein)
        inputCarbs = findViewById(R.id.inputCarbs)

        val logMealBtn = findViewById<Button>(R.id.logMealBtn)
        val saveWeightBtn = findViewById<Button>(R.id.btnSaveWeight)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Get username to display
        val name = prefs.getString("user_name", "User")
        binding.headerTitle.text = "Welcome, $name"

        // 2. Button Listeners
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

        saveWeightBtn.setOnClickListener {
            showWeightEntryDialoug()
        }

        // 3. Navigation
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
    } // End of onCreate



    // Allow recent nutrition to run everytime activity is opened
    override fun onResume() {
        super.onResume()
        fetchDietSummary()
    }

    private fun fetchDietSummary() {
        lifecycleScope.launch {
            try {
                val token = prefs.getString("auth_token", null)
                val final_token = "Bearer " + token
                val response = dietService.getDiets(final_token)
                if (response.isSuccessful && response.body() != null) {
                    updateUI(response.body()!!)
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Failed to fetch: ${e.message}")
            }
        }
    }

    private fun updateUI(data: getDietsResponse) {
        // Update your UI elements safely
        binding.caloriesValue.text = "${data.calories}"
        binding.proteinValue.text = "${data.protein}g"
        binding.carbsValue.text = "${data.carbs}g"
    }

    // --- MEAL LOGGING (Retrofit) ---
    private fun saveMealToDatabase(name: String, calories: Int, protein: Int, carbs: Int) {
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val token = sharedPref.getString("auth_token", "") ?: ""
        val authHeader = "Bearer $token"

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.dietApiService.addDiet(
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
    } // This brace was missing in your code!

    // --- WEIGHT LOGGING (OkHttp) ---
    private fun showWeightEntryDialoug() {
        val weightInput = EditText(this)
        weightInput.hint = "e.g. 180.5"
        weightInput.inputType = EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_FLAG_DECIMAL

        AlertDialog.Builder(this)
            .setTitle("Update Your Weight")
            .setMessage("Enter your current weight below:")
            .setView(weightInput)
            .setPositiveButton("Update Now") { _, _ ->
                val weightValue = weightInput.text.toString()
                if (weightValue.isNotEmpty()) {
                    performWeightUpdate(weightValue)
                } else {
                    Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performWeightUpdate(weight: String) {
        val url = "https://gleaming-sparkle-production-acb6.up.railway.app/health/log-weight"

        val prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId = prefs.getInt("current_user_id:", 5)

        val jsonPayload = """
            {
                "profile_id": $userId
                ,
                "weight": $weight
            }
        """.trimIndent()

        val body = jsonPayload.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder().url(url).post(body).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@HomePage, "Network Error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@HomePage, "Weight synced", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@HomePage, "Failed: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}