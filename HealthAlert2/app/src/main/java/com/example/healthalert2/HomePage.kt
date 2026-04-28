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


    //Exercise inputs
    private lateinit var inputExerciseName: EditText
    private lateinit var inputSets: EditText
    private lateinit var inputReps: EditText
    private lateinit var inputExerciseWeight: EditText

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        // 1. Initialize Views
        inputName = findViewById(R.id.inputName)
        inputCalories = findViewById(R.id.inputCalories)
        inputProtein = findViewById(R.id.inputProtein)
        inputCarbs = findViewById(R.id.inputCarbs)

        //added by NNicholas - for exercise
        inputExerciseName = findViewById(R.id.inputExerciseName)
        inputSets = findViewById(R.id.inputSets)
        inputReps = findViewById(R.id.inputReps)
        inputExerciseWeight = findViewById(R.id.inputExerciseWeight)

        val logMealBtn = findViewById<Button>(R.id.logMealBtn)
        val saveWeightBtn = findViewById<Button>(R.id.btnSaveWeight)
        val btnSaveExercise = findViewById<Button>(R.id.btnSaveWeight)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

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

        btnSaveExercise.setOnClickListener {
            val exercise_name = inputExerciseName.text.toString().trim()
            val sets = inputSets.text.toString().trim()
            val reps = inputReps.text.toString().trim()
            val weightUsed = inputExerciseWeight.text.toString().trim()

            if (exercise_name.isNotEmpty() && sets.isNotEmpty() && reps.isNotEmpty() && weightUsed.isNotEmpty())
            {
                saveExerciseToDatabase(exercise_name, sets, reps, weightUsed)
            }
            else
            {
                Toast.makeText(this, "Please fill in all exercise fields", Toast.LENGTH_SHORT).show()
            }
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

    // --- MEAL LOGGING (Retrofit) ---
    private fun saveMealToDatabase(name: String, calories: Int, protein: Int, carbs: Int) {
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val token = sharedPref.getString("auth_token", "") ?: ""
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

    private fun saveExerciseToDatabase(exercise_name: String, sets: String, reps: String, weightUsed: String)
    {
        val url = "https://gleaming-sparkle-production-acb6.up.railway.app/health/log-exercise"
        val prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userId = prefs.getInt("current_user_id:", 5)

        val jsonPayload = """
            {
                "profile_id": $userId,
                "exercise_name": "$exercise_name",
                "sets": $sets,
                "reps": $reps,
                "weight": $weightUsed
            }
        """.trimIndent()

        val body = jsonPayload.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder().url(url).post(body).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: okio.IOException) {
                runOnUiThread { Toast.makeText(this@HomePage, "Server Error", Toast.LENGTH_SHORT).show() }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseCode = response.code
                runOnUiThread {
                    if (response.isSuccessful)
                    {
                        Toast.makeText(this@HomePage, "Exercise Saved!", Toast.LENGTH_SHORT).show()
                        inputExerciseName.text.clear()
                        inputSets.text.clear()
                        inputReps.text.clear()
                        inputExerciseWeight.text.clear()
                    }
                    else
                    {
                        Log.e("SERVER_ERROR", "Code: $responseCode")
                        Toast.makeText(this@HomePage, "Failed to save exercise", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}