package com.example.healthalert2

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.healthalert2.data.network.GeneratePlanRequest
import com.example.healthalert2.data.network.WorkoutPlan
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
                val hardcodedRequest = request.copy(profile_id = 5)
                val response = repository.getWorkoutPlan(hardcodedRequest)

                if (response.isSuccessful) {
                    val responseBody = response.body()

                    val newPlan = responseBody?.data

                    if (newPlan != null && newPlan is List<*>) {
                        val currentList = activePlansResult.value?.data?.toMutableList() ?: mutableListOf()
                        currentList.addAll(newPlan as List<WorkoutPlan>)
                        activePlansResult.postValue(WorkoutResponse(success = true, data = currentList))
                    } else if (newPlan != null) {
                        val currentList = activePlansResult.value?.data?.toMutableList() ?: mutableListOf()
                        currentList.add(newPlan as WorkoutPlan)
                        activePlansResult.postValue(WorkoutResponse(success = true, data = currentList))
                    }

                    Log.d("API_DEBUG", "Generated successfully for Profile 4")
                } else {
                    errorMessage.value = "Server Error: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage.value = "Network Error: ${e.localizedMessage}"
                Log.e("API_DEBUG", "Error: ${e.message}")
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