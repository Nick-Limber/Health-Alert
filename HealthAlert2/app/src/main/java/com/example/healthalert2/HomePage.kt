package com.example.healthalert2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.healthalert2.data.network.RetrofitClient
import com.example.healthalert2.data.network.AddDietRequest // Ensure this matches your actual data class name
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import android.widget.Toast
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//added by Nicholas
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import android.widget.Button
import okhttp3.Request
  
class HomePage : AppCompatActivity() {
  
    private lateinit var inputName: EditText
    private lateinit var inputCalories: EditText
    private lateinit var inputProtein: EditText
    private lateinit var inputCarbs: EditText

    private val client = OkHttpClient()

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
        //added by Nicholas
        val saveWeightBtn = findViewById<Button>(R.id.btnSaveWeight)

        saveWeightBtn.setOnClickListener {
            showWeightEntryDialoug()
        }


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
    //added by Nicholas
    private fun showWeightEntryDialoug() {
        val weightInput = android.widget.EditText(this)
        weightInput.hint = "e.g. 180.5"
        weightInput.inputType = android.view.inputmethod.EditorInfo.TYPE_CLASS_NUMBER or
                android.view.inputmethod.EditorInfo.TYPE_NUMBER_FLAG_DECIMAL

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Update Your Weight")
            .setMessage("Ready to update your progress? Enter your current weight below:")
            .setView(weightInput)
            .setPositiveButton("Update Now") { _, _ ->
                val weightValue = weightInput.text.toString()

                if (weightValue.isNotEmpty())
                {
                    performWeightUpdate(weightValue)
                }
                else
                {
                    Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performWeightUpdate(weight: String) {
        val url = "https://gleaming-sparkle-production-acb6.up.railway.app/health/log-weight"

        Log.d("APP_TEST", "Sending weight $weight to $url")

        val jsonPayload = """
            {
                "profile_id": 1,
                "weight": $weight
            }
            """.trimIndent()

        val body = jsonPayload.toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("APP_TEST", "Network Error: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@HomePage, "Server error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()

                Log.d("APP_TEST", "Response recieved: ${response.code} - $responseBody")

                runOnUiThread {
                    if (response.isSuccessful)
                    {
                        Toast.makeText(this@HomePage, "Weight synced", Toast.LENGTH_LONG).show()
                    }
                    else
                    {
                        Toast.makeText(this@HomePage, "Update failed: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }
                response.close()
            }
        })
    }
}