package com.example.healthalert2

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
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
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        val tvTimestamp: TextView = itemView.findViewById(R.id.tvTimestamp)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
        val repliesLayout: LinearLayout = itemView.findViewById(R.id.repliesLayout)
        val btnReply: Button = itemView.findViewById(R.id.btnReply)
        val btnShowReplies: Button = itemView.findViewById(R.id.btnShowReplies)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        val context = holder.itemView.context

        // Display post content
        holder.tvTitle.text = post.title
        holder.tvContent.text = post.content
        holder.tvTimestamp.text = getTimeAgo(post.timestamp)

        // Edit post on long click
        holder.itemView.setOnLongClickListener {
            val intent = Intent(context, CreatePostActivity::class.java).apply {
                putExtra("postId", post.postId)
                putExtra("title", post.title)
                putExtra("content", post.content)
            }
            context.startActivity(intent)
            true
        }

        // Delete post
        holder.btnDelete.setOnClickListener {
            val currentPosition = holder.adapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                val postId = postList[currentPosition].postId
                RetrofitInstance.api.deletePost(postId).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            postList.removeAt(currentPosition)
                            notifyItemRemoved(currentPosition)
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

        // Helper to populate replies in UI
        fun updateRepliesUI(replies: List<Reply>) {
            holder.repliesLayout.removeAllViews()
            for (reply in replies) {
                val replyView = TextView(context).apply {
                    text = "↳ User ${reply.userId}: ${reply.content}"
                    setPadding(16, 4, 0, 4)
                    textSize = 14f
                }
                holder.repliesLayout.addView(replyView)
            }
        }

        // Initial setup for replies (show what's already in memory)
        updateRepliesUI(post.replies ?: emptyList())
        holder.repliesLayout.visibility = View.GONE
        holder.btnShowReplies.text = "Show Replies"

        // Toggle replies AND fetch from MySQL
        holder.btnShowReplies.setOnClickListener {
            if (holder.repliesLayout.visibility == View.GONE) {
                // Fetch fresh replies from backend
                RetrofitInstance.api.getReplies(post.postId).enqueue(object : Callback<List<Reply>> {
                    override fun onResponse(call: Call<List<Reply>>, response: Response<List<Reply>>) {
                        if (response.isSuccessful) {
                            val fetchedReplies = response.body() ?: emptyList()

                            // 1. Update local post data so it persists
                            val currentPos = holder.adapterPosition
                            if (currentPos != RecyclerView.NO_POSITION) {
                                postList[currentPos] = postList[currentPos].copy(replies = fetchedReplies)
                            }

                            // 2. Update UI
                            updateRepliesUI(fetchedReplies)
                            holder.repliesLayout.visibility = View.VISIBLE
                            holder.btnShowReplies.text = "Hide Replies"

                            if (fetchedReplies.isEmpty()) {
                                Toast.makeText(context, "No replies yet", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Failed to load replies", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<List<Reply>>, t: Throwable) {
                        Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                holder.repliesLayout.visibility = View.GONE
                holder.btnShowReplies.text = "Show Replies"
            }
        }

        // Reply button (safe context for AlertDialog)
        holder.btnReply.setOnClickListener {
            val activity = context as? android.app.Activity
            activity?.let { act ->
                val input = EditText(context)
                AlertDialog.Builder(act)
                    .setTitle("Reply to Post")
                    .setView(input)
                    .setPositiveButton("Send") { _, _ ->
                        val content = input.text.toString()
                        if (content.isNotEmpty()) {
                            sendReply(post.postId, currentUserId, content, context, holder)
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            } ?: run {
                Toast.makeText(context, "Unable to show reply dialog", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = postList.size

    fun updatePosts(newPosts: List<Post>) {
        postList.clear()
        postList.addAll(newPosts)
        notifyDataSetChanged()
    }

    private fun sendReply(postId: Int, userId: Int, content: String, context: Context, holder: PostViewHolder) {
        val request = CreateReplyRequest(userId, content)
        RetrofitInstance.api.sendReply(postId, request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Reply sent!", Toast.LENGTH_SHORT).show()
                    // Re-trigger the fetch to show the new reply
                    if (holder.repliesLayout.visibility == View.VISIBLE) {
                        // Refresh if already open
                        holder.repliesLayout.visibility = View.GONE
                        holder.btnShowReplies.performClick()
                    } else {
                        // open it
                        holder.btnShowReplies.performClick()
                    }
                } else {
                    Toast.makeText(context, "Failed to send reply", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    fun getTimeAgo(timestamp: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")

            val past = inputFormat.parse(timestamp)?.time ?: return timestamp
            val now = System.currentTimeMillis()

            val diff = now - past

            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24

            when {
                seconds < 60 -> "Just now"
                minutes < 60 -> "$minutes min ago"
                hours < 24 -> "$hours hr ago"
                days < 7 -> "$days day${if (days > 1) "s" else ""} ago"
                else -> {
                    val outputFormat = SimpleDateFormat("MMM d", Locale.getDefault())
                    outputFormat.format(Date(past))
                }
            }
        } catch (e: Exception) {
            timestamp // fallback if parsing fails
        }
    }
}