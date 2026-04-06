package com.example.healthalert2

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthalert2.data.network.WorkoutData // Corrected Import
class PlanAdapter(private var plans: List<WorkoutData>) :
    RecyclerView.Adapter<PlanAdapter.PlanViewHolder>() {

    class PlanViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvPlanName: TextView = view.findViewById(R.id.tvPlanName)
        val tvPlanGoal: TextView = view.findViewById(R.id.tvPlanGoal)
        val ivArrow: ImageView = view.findViewById(R.id.ivDropDownArrow)
        val rvDays: RecyclerView = view.findViewById(R.id.rvDays)
        val planHeader: View = view.findViewById(R.id.planHeader)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.exercise_plan, parent, false)
        return PlanViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        val workoutData = plans[position]

        holder.tvPlanName.text = workoutData.workoutName ?: "Custom Plan"
        holder.tvPlanGoal.text = "Goal: ${workoutData.goal}"

        // Setup nested RecyclerView
        val dayAdapter = DayAdapter(workoutData.days)
        holder.rvDays.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.rvDays.adapter = dayAdapter

        holder.planHeader.setOnClickListener {
            val isExpanded = holder.rvDays.visibility == View.VISIBLE
            holder.rvDays.visibility = if (isExpanded) View.GONE else View.VISIBLE
            holder.ivArrow.rotation = if (isExpanded) 0f else 180f
        }
    }

    fun updateData(newPlans: List<WorkoutData>) {
        this.plans = newPlans
        Log.d("ADAPTER_DEBUG", "Adapter updated with ${newPlans.size} plan(s)")
        notifyDataSetChanged()
    }

    override fun getItemCount() = plans.size
}