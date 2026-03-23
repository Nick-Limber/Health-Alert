package com.example.healthalert2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageView
import com.example.healthalert2.data.network.retrofitClient
import com.example.healthalert2.data.network.ApiService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class MembershipsPage : AppCompatActivity() {
    private lateinit var upgradeButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memberships_page)

        upgradeButton = findViewById<Button>(R.id.upgradeButton)
        val closeButton = findViewById<ImageView>(R.id.closeButton)

        closeButton.setOnClickListener {
            finish()
        }
        upgradeButton.setOnClickListener {

            upgradeMembership(userId = 1) //replace with real id

        }
    }

    private fun upgradeMembership(userId: Int) {
        val apiService = retrofitClient.instance.create(ApiService::class.java)
        val data = mapOf("userId" to userId)

        apiService.upgradeUser(data).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) showPremiumUI()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
    private fun showPremiumUI() {
        upgradeButton.text = "Premium Activated"
        upgradeButton.isEnabled = false
    }

}