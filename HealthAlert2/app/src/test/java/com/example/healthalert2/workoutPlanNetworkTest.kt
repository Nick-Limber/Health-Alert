package com.example.healthalert2

import com.example.healthalert2.data.network.WorkoutResponse
import com.example.healthalert2.data.network.GeneratePlanRequest
import com.example.healthalert2.data.network.RetrofitClient
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*

class WorkoutNetworkTest {
    @Test
    fun testWorkoutApiCall() = runBlocking {
        val request = GeneratePlanRequest(
            profile_id = 5,
            height = 72,
            weight = 170,
            age = 22,
            goal = "weight loss",
            muscle = "chest",
            level = "beginner",
            access = "bodyweight",
            workout_name = "none"
        )

        val response = RetrofitClient.generatePlanApiService.generateWorkoutPlan(request)

        println("Response Code: ${response.code()}")

        if (response.isSuccessful) {
            val body = response.body()
            assertNotNull("Response body should not be null", body)

            val workoutData = body?.data
            assertNotNull("Workout data object should not be null", workoutData)

            // FIX: Use dayNumber instead of day_number
            println("Success! Found ${workoutData?.days?.size} days of workouts.")
            assertEquals(true, body?.success)

            println("\n=== GENERATED WORKOUT PLAN ===")
            println("Goal: ${workoutData?.plan?.goal}")

            workoutData?.days?.forEach { day ->
                // FIX: Use dayNumber
                println("\nDay ${day.dayNumber}:")
                day.exercises.forEach { exercise ->
                    // FIX: Use exerciseName, muscleTarget, and category
                    println("  - ${exercise.exerciseName} (${exercise.category})")
                    println("    Target: ${exercise.muscleTarget}")
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
        val response = RetrofitClient.generatePlanApiService.fetchWorkoutPlan(5)

        if (response.isSuccessful) {
            val body = response.body()
            assertNotNull(body)

            val workoutData = body?.data
            assertNotNull("No plan found in database for this ID", workoutData)

            assertEquals(true, body?.success)
            // FIX: Use workoutData?.plan?.goal
            println("Successfully retrieved plan for goal: ${workoutData?.plan?.goal}")
        } else {
            fail("Fetch API call failed with code: ${response.code()}")
        }
    }
}