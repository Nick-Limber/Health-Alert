package com.example.healthalert2

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(private var postList: MutableList<Post>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

        //holds references to UI elements for each post
    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        val tvTimestamp: TextView = itemView.findViewById(R.id.tvTimestamp)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    //creates each post card layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    //binds the data to each post
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]

        //displays the post data on the screen
        holder.tvTitle.text = post.title
        holder.tvContent.text = post.content
        holder.tvTimestamp.text = post.timestamp

        // EDIT FEATURE -> HOLD CLICK ON A POST ALLOWS TO EDIT
        holder.itemView.setOnLongClickListener {
            val intent = Intent(holder.itemView.context, CreatePostActivity::class.java)
            intent.putExtra("postId", post.postId)
            intent.putExtra("title", post.title)
            intent.putExtra("content", post.content)
            holder.itemView.context.startActivity(intent)
            true
        }

        // DELETE BUTTON
        holder.btnDelete.setOnClickListener {
            val currentPosition = holder.adapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                val postId = postList[currentPosition].postId //gets correct postID to delete

                // Call backend via Retrofit
                RetrofitInstance.api.deletePost(postId).enqueue(object : retrofit2.Callback<Void> {
                    override fun onResponse(
                        call: retrofit2.Call<Void>,
                        response: retrofit2.Response<Void>
                    ) {
                        if (response.isSuccessful) {
                            // Remove from RecyclerView if successfully deletes
                            postList.removeAt(currentPosition)
                            notifyItemRemoved(currentPosition)
                        } else {
                            Toast.makeText(
                                holder.itemView.context,
                                "Failed to delete post",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
//if network fails will show connection error
                    override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                        Toast.makeText(
                            holder.itemView.context,
                            "Error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        }
    }

    override fun getItemCount(): Int = postList.size

    //replaces entire list when fetching from backend
    fun updatePosts(newPosts: List<Post>) {
        postList.clear()
        postList.addAll(newPosts)
        notifyDataSetChanged()
    }

    //adds new post to top of list
    fun addPost(post: Post) {
        postList.add(0, post)
        notifyItemInserted(0)
    }
}