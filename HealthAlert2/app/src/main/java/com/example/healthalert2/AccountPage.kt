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

//added by Nicholas
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class AccountPage : AppCompatActivity() {
    private lateinit var profileImage: ImageView
    private val PICK_IMAGE = 1

    //added by Nicholas
    private val client = OkHttpClient()

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

        //added by Nicholas
        val deleteAccount = findViewById<MaterialCardView>(R.id.deleteAccountContainer)

        deleteAccount.setOnClickListener {
            showDeleteConfirmationDialog()
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

                R.id.workout_plan -> {
                    startActivity(Intent(this, WorkoutPlanActivity::class.java))
                    true
                }

                R.id.nav_past_data -> {
                    val intent = Intent(this, ViewPastDataActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    //added by Nicholas
    private fun showDeleteConfirmationDialog() {

        val dialogView = layoutInflater.inflate(R.layout.dialouge_reauthenticator, null)
        val emailInput = dialogView.findViewById<android.widget.EditText>(R.id.reAuthEmail)
        val passInput = dialogView.findViewById<android.widget.EditText>(R.id.reAuthPassword)

        AlertDialog.Builder(this)
            .setTitle("Confrim Hard Deletion")
            .setMessage("This will permanently delete your data.")
            .setView(dialogView)
            .setPositiveButton("Delete Forever") { _, _ ->
                val email = emailInput.text.toString()
                val password = passInput.text.toString()

                if (email.isNotEmpty() && password.isNotEmpty())
                {
                    performAccountDeletion(email, password)
                }
                else
                {
                    Toast.makeText(this, "Please enter your credentials", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    //added by Nicholas
    private fun performAccountDeletion(email: String, pass: String)
    {
        val url = "http://10.0.2.2:5005/api/delete-account"

        val jsonPayload = """
            {
                "email:": "$email",
                "password": "$pass"
            }
            """.trimIndent()

        val body = jsonPayload.toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(url)
            .delete(body)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { Toast.makeText(this@AccountPage, "Sever error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun  onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AccountPage, "Account has been deleted.", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@AccountPage, LoginPage::class.java)

                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@AccountPage, "Deletion failed: Invalid credentials", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            profileImage.setImageURI(imageUri)
        }
    }


}