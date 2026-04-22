package com.example.healthalert2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
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

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        //added by Nicholas
        val saveWeightBtn = findViewById<Button>(R.id.btnSaveWeight)

        saveWeightBtn.setOnClickListener {
            showWeightEntryDialoug()
        }


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
                R.id.nav_past_data -> {
                    val intent = Intent(this, ViewPastDataActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
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