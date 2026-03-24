package com.example.healthalert2

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
import com.example.healthalert2.data.network.retrofitClient
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
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val apiService = retrofitClient.generatePlanApiService
        val repository = GenerateWorkoutRepository(apiService)
        val factory = WorkoutViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[WorkoutViewModel::class.java]

        val recyclerView = findViewById<RecyclerView>(R.id.rvMainDays)
        recyclerView.layoutManager = LinearLayoutManager(this)
        planAdapter = PlanAdapter(emptyList())
        recyclerView.adapter = planAdapter

        viewModel.activePlansResult.observe(this) { response ->
            if (response != null && response.success) {
                Log.d("ADAPTER_DEBUG", "Received ${response.data.size} plans")

                planAdapter.updateData(response.data)

                // If a new plan was just added, scroll to the bottom automatically
                if (response.data.isNotEmpty()) {
                    recyclerView.postDelayed({
                        recyclerView.smoothScrollToPosition(response.data.size - 1)
                    }, 300) // Small delay to allow the adapter to finish drawing
                }
            } else {
                Log.e("API_ERROR", "Failed to load plans or response was null")
            }
        }

        findViewById<FloatingActionButton>(R.id.fabGenerate).setOnClickListener {
            val bottomSheet = GeneratePlanBottomSheet { request ->
                // This calls the ViewModel function we updated with the hardcoded ID
                viewModel.generateNewPlan(request)
                Toast.makeText(this, "Generating your plan...", Toast.LENGTH_SHORT).show()
            }
            bottomSheet.show(supportFragmentManager, "GeneratePlanSheet")
        }

        viewModel.fetchPlans(5)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setOnItemSelectedListener {
            when(it.itemId) {

                R.id.nav_home -> {
                    val intent = Intent(this, HomePage::class.java)
                    startActivity(intent)
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

}