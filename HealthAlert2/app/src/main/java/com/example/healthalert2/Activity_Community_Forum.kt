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

//allows users to browse posts
class CommunityForumActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnCreatePost: Button
    private lateinit var postList: MutableList<Post>
    private lateinit var adapter: PostAdapter

    private val currentUserId = 1 // TODO: Replace with real logged-in user ID

    private val createPostLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            fetchPosts()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community_forum)

        recyclerView = findViewById(R.id.recyclerViewPosts)
        btnCreatePost = findViewById(R.id.btnCreatePost)

        postList = mutableListOf()
        adapter = PostAdapter(postList, currentUserId)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fetchPosts()

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
                    startActivity(Intent(this, HomePage::class.java))
                    true
                }
                R.id.nav_forum -> true 
                R.id.nav_account -> {
                    startActivity(Intent(this, AccountPage::class.java))
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

    private fun fetchPosts() {
        RetrofitInstance.api.getPosts().enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    response.body()?.let { posts ->
                        val sortedPosts = posts.sortedByDescending { it.timestamp }
                        adapter.updatePosts(sortedPosts)
                        if (sortedPosts.isNotEmpty()) recyclerView.scrollToPosition(0)
                    }
                } else {
                    Toast.makeText(this@CommunityForumActivity, "Failed to load posts", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Toast.makeText(this@CommunityForumActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
