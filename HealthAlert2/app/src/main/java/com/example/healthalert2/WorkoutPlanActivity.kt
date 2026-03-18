package com.example.healthalert2

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthalert2.data.network.RetrofitClient
import com.example.healthalert2.data.repository.GenerateWorkoutRepository
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

        val apiService = RetrofitClient.generatePlanApiService
        val repository = GenerateWorkoutRepository(apiService)
        val factory = WorkoutViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[WorkoutViewModel::class.java]

        val recyclerView = findViewById<RecyclerView>(R.id.rvMainDays)
        recyclerView.layoutManager = LinearLayoutManager(this)
        planAdapter = PlanAdapter(emptyList())
        recyclerView.adapter = planAdapter

        viewModel.activePlansResult.observe(this) { response ->
            response?.let {
                planAdapter.updateData(it.data)
                Log.d("API_DEBUG", "Fetched ${it.data.size} plans")
            }
        }

        viewModel.workoutPlanResult.observe(this) { response ->
            response?.let {
                planAdapter.updateData(it.data)
                Log.d("API_DEBUG", "Generated ${it.data.size} plans")

            }
        }

        findViewById<FloatingActionButton>(R.id.fabGenerate).setOnClickListener {
            val bottomSheet = GeneratePlanBottomSheet { request ->
                viewModel.generateNewPlan(request)
            }
            bottomSheet.show(supportFragmentManager, "GeneratePlanSheet")
        }

        viewModel.fetchPlans(4)
    }
}