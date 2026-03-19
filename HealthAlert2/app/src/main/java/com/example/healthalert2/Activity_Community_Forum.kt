package com.example.healthalert2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Toast

class CommunityForumActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnCreatePost: Button
    private lateinit var postList: MutableList<Post>
    private lateinit var adapter: PostAdapter

    private val createPostLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result -> //only continue if user successfully created a post
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val title = data?.getStringExtra("title") ?: "No title"
            val content = data?.getStringExtra("content") ?: "No content"

            // Creates a new post object and adds to the top of the list
            postList.add(0, Post(postList.size + 1, 1, title, content, "Just now"))

            // Tells recycler a new item was inserted and refresh only the new item
            adapter.notifyItemInserted(0)

            // Scrolls to top so user sees new post immediately
            recyclerView.scrollToPosition(0)
        }
    }

//TEMPORARY COMMUNITY FORUM POSTS
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community_forum)

        recyclerView = findViewById(R.id.recyclerViewPosts)
        btnCreatePost = findViewById(R.id.btnCreatePost)

        postList = mutableListOf(
            Post(1, 1, "Welcome!", "Welcome to the health forum.", "1h ago"),
            Post(2, 2, "Tips", "Drink more water today!", "2h ago")
        )

        adapter = PostAdapter(postList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

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