package com.example.healthalert2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommunityForumActivity : AppCompatActivity() {

    //UI Components
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnCreatePost: Button

    //Data + Adapter
    private lateinit var postList: MutableList<Post>
    private lateinit var adapter: PostAdapter

    // Handles result when returning from CreatePostActivity
    private val createPostLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        //if user created/edited a post successfully
        if (result.resultCode == Activity.RESULT_OK) {
            fetchPosts() // refresh after create or edit
        }
    }

    // Fetch posts from backend (GET request)
    fun fetchPosts() {
        RetrofitInstance.api.getPosts().enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    // Get posts from response
                    response.body()?.let { posts ->
                        //Sort posts by newest first
                        val sortedPosts = posts.sortedByDescending { it.timestamp }
                        //update recyclerview with new data
                        adapter.updatePosts(sortedPosts)
                      //scroll to top if there are posts
                        if (sortedPosts.isNotEmpty()) {
                            recyclerView.scrollToPosition(0)
                        }
                    }
                } else {
                    //server responded but not successful
                    Toast.makeText(
                        this@CommunityForumActivity,
                        "Failed to load posts",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
//runs if request failed
            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Toast.makeText(
                    this@CommunityForumActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    // Called when activity is first created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community_forum)

        // connect UI elements from XML
        recyclerView = findViewById(R.id.recyclerViewPosts)
        btnCreatePost = findViewById(R.id.btnCreatePost)

        // initialize empty list + adapter
        postList = mutableListOf()
        adapter = PostAdapter(postList)

        //set recyclerview layout and attach adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Initial load when page opens
        fetchPosts()

        // Create Post button
        btnCreatePost.setOnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            createPostLauncher.launch(intent)
        }

        //NAVBAR SECTION
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNav.selectedItemId = R.id.nav_forum

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {

                R.id.nav_home -> {
                    val intent = Intent(this, HomePage::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_forum -> {
                    true // already on community forum page
                }

                R.id.nav_account -> {
                    val intent = Intent(this, AccountPage::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }
}
