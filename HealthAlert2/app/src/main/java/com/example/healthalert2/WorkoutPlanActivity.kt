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
                val planList = response.data // This is now a List<WorkoutData>

                // Log the number of plans received
                Log.d("ADAPTER_DEBUG", "Received ${planList.size} plans")

                planAdapter.updateData(planList)

                // Smooth scroll to the newest plan (the last one in the list)
                if (planList.isNotEmpty()) {
                    recyclerView.postDelayed({
                        val lastIndex = planList.size - 1
                        recyclerView.smoothScrollToPosition(lastIndex)
                    }, 300)
                }
            } else {
                Log.e("API_ERROR", "Failed to load plans or response was null")
            }
        }
        findViewById<FloatingActionButton>(R.id.fabGenerate).setOnClickListener {
            val bottomSheet = GeneratePlanBottomSheet { request ->
                viewModel.generateNewPlan(request)
                Toast.makeText(this, "Generating your plan...", Toast.LENGTH_SHORT).show()
            }
            bottomSheet.show(supportFragmentManager, "GeneratePlanSheet")
        }

        viewModel.fetchPlans(7)

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

                else -> false
            }
        }
    }

}