package com.example.healthalert2

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.healthalert2.data.network.GeneratePlanRequest
import com.example.healthalert2.data.network.WorkoutResponse
import com.example.healthalert2.data.repository.GenerateWorkoutRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WorkoutViewModel(private val repository: GenerateWorkoutRepository) : ViewModel() {

    val workoutPlanResult = MutableLiveData<WorkoutResponse?>()
    val activePlansResult = MutableLiveData<WorkoutResponse?>()
    val isLoading = MutableLiveData<Boolean>(false)
    val errorMessage = MutableLiveData<String?>()

    // 1. Pass the token here from the Activity
    fun generateNewPlan(token: String, request: GeneratePlanRequest) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            try {
                val authHeader = "Bearer $token"
                val response = repository.getWorkoutPlan(authHeader, request)

                if (response.isSuccessful) {
                    // Refresh the plans using the same token
                    fetchPlans(token)
                } else {
                    errorMessage.value = "Server Error: ${response.code()}"
                    Log.e("API_ERROR", "Code: ${response.code()}")
                }
            } catch (e: Exception) {
                errorMessage.value = "Network Error: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }

    // 2. Updated to use the Token string
    fun fetchPlans(token: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val authHeader = "Bearer $token"

                // Pass the authHeader instead of profileId
                val response = repository.getPlans(authHeader)

                Log.d("API_DEBUG", "Fetch Plans Response Code: ${response.code()}")
                if (response.isSuccessful) {
                    activePlansResult.postValue(response.body())
                } else {
                    errorMessage.value = "Fetch Error: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage.value = e.localizedMessage
            } finally {
                isLoading.value = false
            }
        }
    }
}
class WorkoutViewModelFactory(private val repository: GenerateWorkoutRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkoutViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkoutViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}