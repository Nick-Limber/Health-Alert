package com.example.healthalert2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import android.app.AlertDialog

class AccountPage : AppCompatActivity() {
    private lateinit var profileImage: ImageView
    private val PICK_IMAGE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_page)

        profileImage = findViewById(R.id.profileImage)


        profileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE)
        }
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        val memberships = findViewById<MaterialCardView>(R.id.membershipsContainer)
        val settings = findViewById<MaterialCardView>(R.id.settingsContainer)
        val signOut = findViewById<MaterialCardView>(R.id.signOutContainer)

        // MEMBERSHIPS PAGE
        memberships.setOnClickListener {
            val intent = Intent(this, MembershipsPage::class.java)
            startActivity(intent)
        }

        // SETTINGS PAGE
        settings.setOnClickListener {
            val intent = Intent(this, SettingsPage::class.java)
            startActivity(intent)
        }

        signOut.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Sign Out") { _, _ ->

                    val intent = Intent(this, LoginPage::class.java)
                    startActivity(intent)


                    finish()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        bottomNav.selectedItemId = R.id.nav_forum

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {

                R.id.nav_home -> {
                    val intent = Intent(this, HomePage::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_forum -> {
                    val intent = Intent(this, CommunityForumActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_account -> {
                    true //this page
                }

                else -> false
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            profileImage.setImageURI(imageUri)
        }
    }


}