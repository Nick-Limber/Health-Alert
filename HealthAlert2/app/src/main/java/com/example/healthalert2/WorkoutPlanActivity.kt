package com.example.healthalert2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthalert2.data.network.RetrofitClient
import com.example.healthalert2.data.repository.GenerateWorkoutRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class WorkoutPlanActivity : AppCompatActivity() {

    private lateinit var viewModel: WorkoutViewModel
    private lateinit var planAdapter: PlanAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_workout_plan)

        val mainView = findViewById<View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        val apiService = RetrofitClient.generatePlanApiService
        val repository = GenerateWorkoutRepository(apiService)
        val factory = WorkoutViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[WorkoutViewModel::class.java]

        val recyclerView = findViewById<RecyclerView>(R.id.rvMainDays)
        recyclerView.layoutManager = LinearLayoutManager(this)
        planAdapter = PlanAdapter(emptyList())
        recyclerView.adapter = planAdapter

        viewModel.activePlansResult.observe(this) { response ->
            if (response != null && response.success) {
                val planList = response.data
                Log.d("ADAPTER_DEBUG", "Received ${planList.size} plans")

                planAdapter.updateData(planList)

                if (planList.isNotEmpty()) {
                    recyclerView.postDelayed({
                        val lastIndex = planList.size - 1
                        recyclerView.smoothScrollToPosition(lastIndex)
                    }, 300)
                }
            } else {
                Log.e("API_ERROR", "Failed to load plans or unauthorized")
                // If it's a 401 error, you might want to force a logout here
            }
        }

        findViewById<FloatingActionButton>(R.id.fabGenerate).setOnClickListener {
            val bottomSheet = GeneratePlanBottomSheet { request ->

                val token = getSavedToken()

                if (token != null) {
                    viewModel.generateNewPlan(token, request)
                    Toast.makeText(this, "Generating your plan...", Toast.LENGTH_SHORT).show()
                } else {
                    redirectToLogin()
                }
            }
            bottomSheet.show(supportFragmentManager, "GeneratePlanSheet")
        }

        val token = getSavedToken()
        if (token != null) {
            viewModel.fetchPlans(token)
        } else {
            redirectToLogin()
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.workout_plan // Ensure correct item is highlighted

        bottomNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomePage::class.java))
                    true
                }
                R.id.nav_forum -> {
                    startActivity(Intent(this, CommunityForumActivity::class.java))
                    true
                }
                R.id.nav_account -> {
                    startActivity(Intent(this, AccountPage::class.java))
                    true
                }
                R.id.workout_plan -> true // Already here
                R.id.nav_past_data -> {
                    startActivity(Intent(this, ViewPastDataActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }


    // HELPER FUNCTIONS
    private fun getSavedToken(): String? {
        val sharedPrefs = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPrefs.getString("auth_token", null)
    }

    private fun redirectToLogin() {
        Toast.makeText(this, "Please log in again", Toast.LENGTH_LONG).show()
        val intent = Intent(this, LoginPage::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}