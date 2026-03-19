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

    //COMMUNITY FORUM POSTS
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community_forum)

        recyclerView = findViewById(R.id.recyclerViewPosts)
        btnCreatePost = findViewById(R.id.btnCreatePost)

        postList = mutableListOf()
        adapter = PostAdapter(postList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Get posts from backend
        RetrofitInstance.api.getPosts().enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    response.body()?.let { posts ->
                        val sortedPosts = posts.sortedByDescending { it.timestamp }

                        // Use adapter's update method instead of manually clearing postList
                        adapter.updatePosts(sortedPosts)

                        // Scroll to top so newest post at top
                        if (sortedPosts.isNotEmpty()) {
                            recyclerView.scrollToPosition(0)
                        }
                    }
                }
            }
            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Toast.makeText(this@CommunityForumActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                t.printStackTrace()
            }
        })


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