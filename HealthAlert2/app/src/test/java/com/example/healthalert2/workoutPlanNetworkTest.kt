package com.example.healthalert2

import com.example.healthalert2.data.network.GeneratePlanRequest
import com.example.healthalert2.data.network.retrofitClient
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*

class WorkoutNetworkTest {

    @Test
    fun testWorkoutApiCall() = runBlocking {

        val request = GeneratePlanRequest(
            profile_id = 3,
            height = 72,
            weight = 170,
            age = 22,
            goal = "weight loss",
            muscle = "none",
            level = "beginner",
            access = "none",
            workout_name = "none"
        )

        val response = retrofitClient.generatePlanApiService.generateWorkoutPlan(request)

        println("Response Code: ${response.code()}")

        if (response.isSuccessful) {
            val body = response.body()
            println("Success! Found ${body?.data?.days?.size} days of workouts.")

            assertNotNull(body)
            assertEquals(true, body?.success)

            println("\n=== GENERATED WORKOUT PLAN ===")
            println("Workout Name: ${body?.data?.workoutName}")
            println("Goal: ${body?.data?.goal}")

            body?.data?.days?.forEach { day ->
                println("\nDay ${day.dayNumber}:")
                day.exercises.forEach { exercise ->
                    println("  - ${exercise.name} [Order: ${exercise.order}]")
                    println("    Target: ${exercise.target}")
                }
            }
            println("\n===============================")

        } else {
            println("Error Body: ${response.errorBody()?.string()}")
            fail("API call failed with code: ${response.code()}")
        }
    }

    @Test
    fun testFetchActivePlans() = runBlocking {
        // Test for retrieving existing plans
        val response = retrofitClient.generatePlanApiService.fetchWorkoutPlan(3)

        println("Fetch Response Code: ${response.code()}")

        if (response.isSuccessful) {
            val body = response.body()
            assertNotNull(body)
            assertEquals(true, body?.success)
            println("Successfully retrieved plan: ${body?.data?.workoutName}")
        } else {
            fail("Fetch API call failed")
        }
    }
}
