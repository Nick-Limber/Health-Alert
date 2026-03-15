package com.example.healthalert2

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthalert2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginbutton.setOnClickListener {
            // Navigate to CommunityForumActivity for now
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
        }

        binding.joinnowbutton.setOnClickListener {
            Toast.makeText(this, "Join Now Clicked", Toast.LENGTH_LONG).show()

        }

        binding.closeButton.setOnClickListener {
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
            finish()
        }

    }
}