package com.example.healthalert2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import com.example.healthalert2.R
import com.example.healthalert2.data.network.GeneratePlanRequest
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText

class GeneratePlanBottomSheet(
    private val onGenerate: (GeneratePlanRequest) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.popup_generate_plan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Set up the Dropdown Options
        setupDropdowns(view)

        // 2. Handle the Button Click
        view.findViewById<Button>(R.id.btnGenerateSubmit).setOnClickListener {

            val height = view.findViewById<TextInputEditText>(R.id.etHeight).text.toString().toIntOrNull() ?: 0
            val weight = view.findViewById<TextInputEditText>(R.id.etWeight).text.toString().toIntOrNull() ?: 0
            val age = view.findViewById<TextInputEditText>(R.id.etAge).text.toString().toIntOrNull() ?: 0
            val planName = view.findViewById<TextInputEditText>(R.id.etPlanName).text.toString()

            val goal = view.findViewById<AutoCompleteTextView>(R.id.dropdownGoal).text.toString()
            val muscle = view.findViewById<AutoCompleteTextView>(R.id.dropdownMuscle).text.toString()
            val experience = view.findViewById<AutoCompleteTextView>(R.id.dropdownExperience).text.toString()
            val access = view.findViewById<AutoCompleteTextView>(R.id.dropdownAccess).text.toString()

            val request = GeneratePlanRequest(
                profile_id = 3, // You can replace this with a real user ID later
                height = height,
                weight = weight,
                age = age,
                goal = goal,
                muscle = muscle,
                level = experience,
                access = access,
                workout_name = planName
            )

            onGenerate(request)
            dismiss()
        }
    }

    private fun setupDropdowns(view: View) {

        val goals = arrayOf("weight loss", "build muscle", "maintain fitness")
        val goalAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, goals)
        view.findViewById<AutoCompleteTextView>(R.id.dropdownGoal).setAdapter(goalAdapter)

        val muscles = arrayOf("chest","shoulders","triceps","upper_back","lower_back","biceps","forearms","traps","quadriceps","hamstrings","glutes","calves","abs","obliques","full_body","cardio","none")

        val muscleAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, muscles)
        view.findViewById<AutoCompleteTextView>(R.id.dropdownMuscle).setAdapter(muscleAdapter)

        val levels = arrayOf("beginner", "intermediate", "advanced")
        val levelAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, levels)
        view.findViewById<AutoCompleteTextView>(R.id.dropdownExperience).setAdapter(levelAdapter)

        val access = arrayOf("gym_access", "bodyweight")
        val accessAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, access)
        view.findViewById<AutoCompleteTextView>(R.id.dropdownAccess).setAdapter(accessAdapter)
    }
}