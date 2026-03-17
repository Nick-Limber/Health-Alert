package com.example.healthalert2

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthalert2.DayAdapter
import com.example.healthalert2.GeneratePlanBottomSheet
import com.example.healthalert2.WorkoutViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class WorkoutPlanActivity : AppCompatActivity() {

    private lateinit var viewModel: WorkoutViewModel
    private lateinit var dayAdapter: DayAdapter

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

        viewModel = ViewModelProvider(this)[WorkoutViewModel::class.java]

        val recyclerView = findViewById<RecyclerView>(R.id.rvMainDays)
        recyclerView.layoutManager = LinearLayoutManager(this)

        dayAdapter = DayAdapter(emptyList())
        recyclerView.adapter = dayAdapter

        viewModel.workoutPlanResult.observe(this) { response ->
            response?.let {
                dayAdapter = DayAdapter(it.data.days)
                recyclerView.adapter = dayAdapter
            }
        }

        findViewById<FloatingActionButton>(R.id.fabGenerate).setOnClickListener {
            val bottomSheet = GeneratePlanBottomSheet { request ->
                viewModel.generateNewPlan(request)
            }
            bottomSheet.show(supportFragmentManager, "GeneratePlanSheet")
        }

    }
}