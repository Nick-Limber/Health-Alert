package com.example.healthalert2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthalert2.data.network.WorkoutDay

// Changed 'val' to 'var' so we can update the list later
class DayAdapter(private var days: List<WorkoutDay>) :
    RecyclerView.Adapter<DayAdapter.DayViewHolder>() {

    class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDayNumber: TextView = view.findViewById(R.id.tvDayNumber)
        val rvExercises: RecyclerView = view.findViewById(R.id.rvExercises)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.exercise_day, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = days[position]
        holder.tvDayNumber.text = "Day ${day.dayNumber}"

        val exerciseAdapter = ExerciseAdapter(day.exercises)
        holder.rvExercises.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.rvExercises.adapter = exerciseAdapter
    }
    fun updateData(newDays: List<WorkoutDay>) {
        this.days = newDays
        notifyDataSetChanged() // This is the magic line that refreshes the screen
    }

    override fun getItemCount() = days.size
    }
