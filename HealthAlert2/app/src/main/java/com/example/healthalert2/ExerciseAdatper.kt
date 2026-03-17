package com.example.healthalert2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthalert2.data.network.Exercise

class ExerciseAdapter(private val exercises: List<Exercise>) :
    RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    class ExerciseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvExerciseName)
        val muscle: TextView = view.findViewById(R.id.tvMuscleTarget)
        val category: TextView = view.findViewById(R.id.tvCategoryTag)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.name.text = exercise.exerciseName ?: "Unknown Exercise"
        holder.muscle.text = "Target: ${exercise.muscleTarget ?: "General"}"
        holder.category.text = exercise.category ?: "Fitness"
    }

    override fun getItemCount() = exercises.size
}