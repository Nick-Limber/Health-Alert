package com.example.healthalert2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthalert2.data.network.WorkoutExercise

class ExerciseAdapter(private val exercises: List<WorkoutExercise>) :
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

        // FIX: Match these to your WorkoutExercise.kt data class fields
        holder.name.text = exercise.exerciseName

        // Use 'muscleTarget' instead of 'target'
        holder.muscle.text = "Target: ${exercise.muscleTarget}"

        // Use 'category' instead of 'order' (as per the new model)
        holder.category.text = exercise.category
    }

    override fun getItemCount() = exercises.size
}