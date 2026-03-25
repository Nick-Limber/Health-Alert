package com.example.healthalert2

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.healthalert2.data.network.retrofitClient
import com.example.healthalert2.data.network.ApiService
import com.example.healthalert2.data.network.MembershipResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MembershipsPage : AppCompatActivity() {

    private lateinit var upgradeButton: Button
    private var membershipStatus: String = "free" // default to free
    private lateinit var freeCard: View
    private lateinit var premiumCard: View
    private lateinit var freePlanLabel: TextView
    private lateinit var premiumPlanLabel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memberships_page)

        upgradeButton = findViewById(R.id.upgradeButton)
        val closeButton = findViewById<ImageView>(R.id.closeButton)

        freeCard = findViewById(R.id.freePlanCard)
        premiumCard = findViewById(R.id.premiumPlanCard)

        // Bind the "Current Plan" labels inside the cards
        freePlanLabel = freeCard.findViewById(R.id.freePlanLabel)
        premiumPlanLabel = premiumCard.findViewById(R.id.premiumPlanLabel)

        closeButton.setOnClickListener {
            finish()
        }

        upgradeButton.setOnClickListener {
            toggleMembership(userId = 2) // Replace with real logged-in user ID
        }

        // Load current membership from backend
        loadMembership(2)
    }

    // Toggle between free and premium
    private fun toggleMembership(userId: Int) {
        val apiService = retrofitClient.instance.create(ApiService::class.java)
        val data = mapOf("userId" to userId)

        apiService.toggleMembership(data).enqueue(object : Callback<MembershipResponse> {
            override fun onResponse(
                call: Call<MembershipResponse>,
                response: Response<MembershipResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    membershipStatus = response.body()!!.plan
                    updateUI()
                }
            }

            override fun onFailure(call: Call<MembershipResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    // Update UI for button and card labels
    private fun updateUI() {
        if (membershipStatus == "premium") {
            upgradeButton.text = "Cancel Premium Plan"
            upgradeButton.setBackgroundColor(getColor(android.R.color.holo_red_dark))
            freePlanLabel.text = ""            // Remove "Current Plan" from free
            premiumPlanLabel.text = "Current Plan"
        } else {
            upgradeButton.text = "Upgrade to Premium"
            upgradeButton.setBackgroundColor(getColor(android.R.color.holo_green_dark))
            freePlanLabel.text = "Current Plan"
            premiumPlanLabel.text = ""         // Remove "Current Plan" from premium
        }
    }

    // Load current membership from backend
    private fun loadMembership(userId: Int) {
        val apiService = retrofitClient.instance.create(ApiService::class.java)
        apiService.getMembership(userId).enqueue(object : Callback<MembershipResponse> {
            override fun onResponse(
                call: Call<MembershipResponse>,
                response: Response<MembershipResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    membershipStatus = response.body()!!.plan
                    updateUI()
                }
            }

            override fun onFailure(call: Call<MembershipResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}