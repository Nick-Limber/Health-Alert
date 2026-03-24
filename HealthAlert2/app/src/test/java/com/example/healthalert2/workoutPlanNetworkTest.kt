package com.example.healthalert2

import com.example.healthalert2.data.network.WorkoutPlan
import com.example.healthalert2.data.network.WorkoutResponse
import com.example.healthalert2.data.network.GeneratePlanRequest
import com.example.healthalert2.data.network.RetrofitClient
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*

class WorkoutNetworkTest {

    @Test
    fun testWorkoutApiCall() = runBlocking {
        // Updated to match your backend's expected fields
        val request = GeneratePlanRequest(
            profile_id = 5,
            height = 72,
            weight = 170,
            age = 22,
            goal = "weight loss",
            muscle = "biceps",
            level = "beginner",
            access = "none",
            workout_name = "none"
        )

        val response = RetrofitClient.generatePlanApiService.generateWorkoutPlan(request)

        println("Response Code: ${response.code()}")

        if (response.isSuccessful) {
            val body = response.body()
            assertNotNull("Response body should not be null", body)

            // Access the first plan in the data list
            val firstPlan = body?.data?.firstOrNull()
            assertNotNull("Plan list should not be empty", firstPlan)

            println("Success! Found ${firstPlan?.days?.size} days of workouts.")
            assertEquals(true, body?.success)

            println("\n=== GENERATED WORKOUT PLAN ===")
            println("Workout Name: ${firstPlan?.workoutName}")
            println("Goal: ${firstPlan?.goal}")

            // Correctly nesting the loops through the first plan
            firstPlan?.days?.forEach { day ->
                println("\nDay ${day.dayNumber}:")
                day.exercises.forEach { exercise ->
                    println("  - ${exercise.name} [Order: ${exercise.order}]")
                    println("    Target: ${exercise.target}")
                }
            }
            println("\n===============================")

        } else {
            val errorMsg = response.errorBody()?.string()
            println("Error Body: $errorMsg")
            fail("API call failed with code: ${response.code()} - $errorMsg")
        }
    }

    @Test
    fun testFetchActivePlans() = runBlocking {
        // Assuming 3 is a valid profile_id or plan_id
        val response = RetrofitClient.generatePlanApiService.fetchWorkoutPlan(3)

        println("Fetch Response Code: ${response.code()}")

        if (response.isSuccessful) {
            val body = response.body()
            assertNotNull(body)

            val firstPlan = body?.data?.firstOrNull()
            assertNotNull("No plans found in database for this ID", firstPlan)

            assertEquals(true, body?.success)
            println("Successfully retrieved plan: ${firstPlan?.workoutName}")
        } else {
            fail("Fetch API call failed with code: ${response.code()}")
        }
    }
}