package com.example.healthalert2

import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class PostAdapter(
    private var postList: MutableList<Post>,
    private val currentUserId: Int
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView? = itemView.findViewById(R.id.tvTitle)
        val tvContent: TextView? = itemView.findViewById(R.id.tvContent)
        val tvTimestamp: TextView? = itemView.findViewById(R.id.tvTimestamp)
        val btnDelete: ImageButton? = itemView.findViewById(R.id.btnDelete)
        val repliesLayout: LinearLayout? = itemView.findViewById(R.id.repliesLayout)
        val btnReply: Button? = itemView.findViewById(R.id.btnReply)
        val btnShowReplies: Button? = itemView.findViewById(R.id.btnShowReplies)
        val tvUsername: TextView? = itemView.findViewById(R.id.tvUsername) 
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        val context = holder.itemView.context

        holder.tvTitle?.text = post.title
        holder.tvContent?.text = post.content
        holder.tvUsername?.text = post.username
        holder.tvTimestamp?.text = getTimeAgo(post.timestamp)

        // Reset visibility and content for recycled views
        holder.repliesLayout?.visibility = View.GONE
        holder.btnShowReplies?.text = "Show Replies"
        holder.repliesLayout?.removeAllViews()

        // EDIT POST - LONG CLICK
        holder.itemView.setOnLongClickListener {
            val intent = Intent(context, CreatePostActivity::class.java).apply {
                putExtra("postId", post.postId)
                putExtra("title", post.title)
                putExtra("content", post.content)
            }
            context.startActivity(intent)
            true
        }

        // DELETE POST
        holder.btnDelete?.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Delete Post")
                .setMessage("Are you sure you want to delete this post?")
                .setPositiveButton("Yes") { _, _ ->
                    val currentPosition = holder.adapterPosition
                    if (currentPosition != RecyclerView.NO_POSITION) {
                        val postId = postList[currentPosition].postId
                        RetrofitInstance.api.deletePost(postId).enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (response.isSuccessful) {
                                    postList.removeAt(currentPosition)
                                    notifyItemRemoved(currentPosition)
                                    Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to delete post", Toast.LENGTH_SHORT).show()
                                }
                            }
                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }


        // SHOW/HIDE REPLIES
        holder.btnShowReplies?.setOnClickListener {
            if (holder.repliesLayout == null) return@setOnClickListener

            if (holder.repliesLayout.visibility == View.GONE) {
                RetrofitInstance.api.getReplies(post.postId).enqueue(object : Callback<List<Reply>> {

                    // --- REPLACE THIS BLOCK ---
                    override fun onResponse(call: Call<List<Reply>>, response: Response<List<Reply>>) {
                        if (response.isSuccessful) {
                            val replies = response.body() ?: emptyList()

                            val currentPos = holder.adapterPosition
                            if (currentPos == RecyclerView.NO_POSITION) return

                            // Use .post to ensure the UI thread is ready for the new views
                            holder.itemView.post {
                                try {
                                    // Update the data list
                                    postList[currentPos] = postList[currentPos].copy(replies = replies)

                                    // 1. Fill the layout with reply views
                                    renderReplies(holder, replies, post.postId, context)

                                    // 2. Make it visible
                                    holder.repliesLayout.visibility = View.VISIBLE
                                    holder.btnShowReplies.text = "Hide Replies"

                                    if (replies.isEmpty()) {
                                        Toast.makeText(context, "No replies yet", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    android.util.Log.e("PostAdapter", "Crash prevented: ${e.message}")
                                }
                            }
                        }
                    }
                    // ---------------------------

                    override fun onFailure(call: Call<List<Reply>>, t: Throwable) {
                        Toast.makeText(context, "Error loading replies", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                holder.repliesLayout.visibility = View.GONE
                holder.btnShowReplies.text = "Show Replies"
            }
        }

        // REPLY BUTTON
        holder.btnReply?.setOnClickListener {
            val input = EditText(context)
            AlertDialog.Builder(context)
                .setTitle("Reply")
                .setView(input)
                .setPositiveButton("Send") { _, _ ->
                    val text = input.text.toString()
                    if (text.isNotEmpty()) {
                        sendReply(post.postId, currentUserId, text, context, holder)
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun renderReplies(holder: PostViewHolder, replies: List<Reply>, postId: Int, context: Context) {
        if (holder.repliesLayout == null) return
        holder.repliesLayout.removeAllViews()
        for (reply in replies) {
            Log.d("DELETE_CHECK", "reply.user=${reply.profile_id}, current=$currentUserId")
            val row = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(16, 6, 0, 6)
            }
            val text = TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                // Use safe access or fallback to "User" if username is missing
               // val displayUser = if (reply.username.isNullOrEmpty()) "User ${reply.profile_id}" else reply.username
                //this.text = "↳ $displayUser: ${reply.content}"
                val displayUser = reply.username ?: "Unknown"

                this.text = "↳ $displayUser: ${reply.content}"
                textSize = 14f
            }
            row.addView(text)
            if (reply.profile_id == currentUserId) {
                val deleteBtn = ImageButton(context).apply {
                    setImageResource(android.R.drawable.ic_menu_delete)
                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                }
                deleteBtn.setOnClickListener {
                    Log.d("DELETE_REPLY", "Button clicked! postId=$postId replyId=${reply.id}")
                    AlertDialog.Builder(context)
                        .setTitle("Delete Reply")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes") { _, _ ->
                            Log.d("DELETE_REPLY", "Yes pressed, calling deleteReply")
                            deleteReply(postId, reply.id, holder, context)
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
                row.addView(deleteBtn)
            }
            holder.repliesLayout.addView(row)
        }
    }

    private fun deleteReply(postId: Int, replyId: Int, holder: PostViewHolder, context: Context) {
        val token = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            .getString("auth_token", "") ?: ""

        RetrofitInstance.api.deleteReply(postId, replyId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Reply deleted", Toast.LENGTH_SHORT).show()
                    RetrofitInstance.api.getReplies(postId).enqueue(object : Callback<List<Reply>> {
                        override fun onResponse(call: Call<List<Reply>>, response: Response<List<Reply>>) {
                            if (response.isSuccessful) {
                                renderReplies(holder, response.body() ?: emptyList(), postId, context)
                            }
                        }
                        override fun onFailure(call: Call<List<Reply>>, t: Throwable) {}
                    })
                } else {
                    Toast.makeText(context, "Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendReply(
        postId: Int,
        userId: Int,
        content: String,
        context: Context,
        holder: PostViewHolder
    ) {
        val prefs = context.getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val username = prefs.getString("user_name", "Unknown") ?: "Unknown"

        RetrofitInstance.api.sendReply(postId, CreateReplyRequest(
            postId = postId,
            profile_id = currentUserId,
            username = username,
            content = content
        )).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Reply sent", Toast.LENGTH_SHORT).show()
                    if (holder.repliesLayout?.visibility == View.GONE) {
                        holder.btnShowReplies?.performClick()
                    } else {
                        RetrofitInstance.api.getReplies(postId).enqueue(object : Callback<List<Reply>> {
                            override fun onResponse(call: Call<List<Reply>>, response: Response<List<Reply>>) {
                                if (response.isSuccessful) {
                                    renderReplies(holder, response.body() ?: emptyList(), postId, context)
                                }
                            }
                            override fun onFailure(call: Call<List<Reply>>, t: Throwable) {}
                        })
                    }
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun getItemCount(): Int = postList.size

    fun updatePosts(newPosts: List<Post>) {
        postList.clear()
        postList.addAll(newPosts)
        notifyDataSetChanged()
    }

    private fun getTimeAgo(timestamp: String?): String {
        if (timestamp == null) return ""
        return try {
            val f = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            f.timeZone = TimeZone.getTimeZone("UTC")
            val date = f.parse(timestamp) ?: return timestamp 

            val diff = System.currentTimeMillis() - date.time
            val minutes = diff / 60000
            val hours = minutes / 60
            val days = hours / 24

            when {
                minutes < 1 -> "Just now"
                minutes < 60 -> "$minutes min ago"
                hours < 24 -> "$hours hr ago"
                else -> "$days days ago"
            }
        } catch (e: Exception) {
            timestamp
        }
    }
}
