package com.example.healthalert2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast

class MembershipsPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memberships_page)

        val upgradeButton = findViewById<Button>(R.id.upgradeButton)
        val closeButton = findViewById<ImageView>(R.id.closeButton)

        closeButton.setOnClickListener {
            finish()
        }
        upgradeButton.setOnClickListener {

            Toast.makeText(
                this,
                "Premium purchase coming soon!",
                Toast.LENGTH_LONG
            ).show()

        }
    }
}