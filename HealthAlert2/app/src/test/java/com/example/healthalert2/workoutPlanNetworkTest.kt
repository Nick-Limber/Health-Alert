package com.example.healthalert2

import com.example.healthalert2.data.network.GeneratePlanRequest
import com.example.healthalert2.data.network.RetrofitClient
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*

class WorkoutNetworkTest {

    @Test
    fun testWorkoutApiCall() = runBlocking {
        // 1. Arrange: Create a fake request based on your JSON
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

        // 2. Act: Call the real API through the RetrofitClient
        val response = RetrofitClient.apiService.generateWorkoutPlan(request)

        // 3. Assert: Check if it worked
        println("Response Code: ${response.code()}")

        if (response.isSuccessful) {
            val body = response.body()
            println("Success! Found ${body?.data?.days?.size} days of workouts.")

            // This checks that the response isn't empty
            assertNotNull(body)
            assertEquals(true, body?.success)
        } else {
            println("Error Body: ${response.errorBody()?.string()}")
            fail("API call failed with code: ${response.code()}")
        }
    }
}