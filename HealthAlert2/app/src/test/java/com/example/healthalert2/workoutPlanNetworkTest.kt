package com.example.healthalert2

import com.example.healthalert2.data.network.GeneratePlanRequest
import com.example.healthalert2.data.network.RetrofitClient
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

        val response = RetrofitClient.apiService.generateWorkoutPlan(request)

        println("Response Code: ${response.code()}")

        if (response.isSuccessful) {
            val body = response.body()
            println("Success! Found ${body?.data?.days?.size} days of workouts.")

            assertNotNull(body)
            assertEquals(true, body?.success)

            println("\n=== GENERATED WORKOUT PLAN ===")
            println("Goal: ${body?.data?.plan?.goal}")
            println("Level: ${body?.data?.plan?.level}")

            body?.data?.days?.forEach { day ->
                println("\nDay ${day.day_number}:")
                day.exercises.forEach { exercise ->
                    println("  - ${exercise.exerciseName} [ID: ${exercise.exerciseId}]")
                    println("    Target: ${exercise.muscleTarget} | Category: ${exercise.category}")
                }
            }
            println("\n===============================")

        } else {
            println("Error Body: ${response.errorBody()?.string()}")
            fail("API call failed with code: ${response.code()}")
        }
    }
}