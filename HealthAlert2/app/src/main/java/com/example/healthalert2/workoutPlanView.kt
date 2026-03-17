package com.example.healthalert2

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthalert2.data.network.GeneratePlanRequest
import com.example.healthalert2.data.network.GeneratePlanResponse
import com.example.healthalert2.data.network.RetrofitClient
import kotlinx.coroutines.launch

class WorkoutViewModel : ViewModel() {

    val workoutPlanResult = MutableLiveData<GeneratePlanResponse?>()
    val isLoading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String?>()

    fun generateNewPlan(request: GeneratePlanRequest) {
        viewModelScope.launch {
            isLoading.postValue(true)
            errorMessage.postValue(null)

            Log.d("API_DEBUG", "Sending Request: $request")

            try {
                val response = RetrofitClient.apiService.generateWorkoutPlan(request)

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("API_DEBUG", "Success! Received: $body")

                    if (body?.data?.days.isNullOrEmpty()) {
                        Log.w("API_DEBUG", "Warning: Response successful but 'days' list is empty!")
                    }

                    workoutPlanResult.postValue(body)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorLog = "Error Code: ${response.code()} | Body: $errorBody"
                    Log.e("API_DEBUG", errorLog)
                    errorMessage.postValue(errorLog)
                }
            } catch (e: Exception) {
                Log.e("API_DEBUG", "Network Failure: ${e.message}", e)
                errorMessage.postValue("Network Failure: ${e.message}")
            } finally {
                isLoading.postValue(false)
            }
        }
    }
}