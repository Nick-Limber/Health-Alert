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

class WorkoutViewModel(private val repository: GenerateWorkoutRepository) : ViewModel() {

    val workoutPlanResult = MutableLiveData<WorkoutResponse?>()
    val activePlansResult = MutableLiveData<WorkoutResponse?>()
    val isLoading = MutableLiveData<Boolean>(false)
    val errorMessage = MutableLiveData<String?>()

    fun generateNewPlan(request: GeneratePlanRequest) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            try {
                val response = repository.getWorkoutPlan(request)
                if (response.isSuccessful) {
                    workoutPlanResult.value = response.body()
                    Log.d("API_DEBUG", "Generated successfully")
                } else {
                    errorMessage.value = "Server Error: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage.value = "Network Error: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }


    fun fetchPlans(profileId: Int) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = repository.getPlans(profileId)
                if (response.isSuccessful) {
                    activePlansResult.value = response.body()
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