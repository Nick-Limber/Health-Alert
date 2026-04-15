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

        holder.tvTitle.text = post.title
        holder.tvContent.text = post.content
        holder.tvTimestamp.text = getTimeAgo(post.timestamp)

        // Reset visibility and content for recycled views
        holder.repliesLayout.visibility = View.GONE
        holder.btnShowReplies.text = "Show Replies"
        holder.repliesLayout.removeAllViews()

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
        holder.btnDelete.setOnClickListener {
            val context = holder.itemView.context

            AlertDialog.Builder(context)
                .setTitle("Delete Post")
                .setMessage("Are you sure you want to delete this post?")
                .setPositiveButton("Yes") { _, _ ->

                    val currentPosition = holder.adapterPosition
                    if (currentPosition != RecyclerView.NO_POSITION) {

                        val postId = postList[currentPosition].postId

                        RetrofitInstance.api.deletePost(postId)
                            .enqueue(object : Callback<Void> {
                                override fun onResponse(
                                    call: Call<Void>,
                                    response: Response<Void>
                                ) {
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
        holder.btnShowReplies.setOnClickListener {
            if (holder.repliesLayout.visibility == View.GONE) {
                RetrofitInstance.api.getReplies(post.postId)
                    .enqueue(object : Callback<List<Reply>> {
                        override fun onResponse(
                            call: Call<List<Reply>>,
                            response: Response<List<Reply>>
                        ) {
                            if (response.isSuccessful) {
                                val replies = response.body() ?: emptyList()

                                if (replies.isEmpty()) {
                                    Toast.makeText(context, "No replies yet", Toast.LENGTH_SHORT).show()
                                    holder.repliesLayout.visibility = View.VISIBLE
                                    holder.btnShowReplies.text = "Hide Replies"
                                    holder.repliesLayout.removeAllViews()
                                    return
                                }

                                val currentPos = holder.adapterPosition
                                if (currentPos != RecyclerView.NO_POSITION) {
                                    postList[currentPos] = postList[currentPos].copy(replies = replies)
                                }

                                renderReplies(holder, replies, post.postId, context)

                                holder.repliesLayout.visibility = View.VISIBLE
                                holder.btnShowReplies.text = "Hide Replies"
                            }
                        }

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
        holder.btnReply.setOnClickListener {
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

    private fun renderReplies(
        holder: PostViewHolder,
        replies: List<Reply>,
        postId: Int,
        context: Context
    ) {
        holder.repliesLayout.removeAllViews()
        for (reply in replies) {
            val row = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(16, 6, 0, 6)
            }

            val text = TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                this.text = "↳ User ${reply.userId}: ${reply.content}"
                textSize = 14f
            }
            row.addView(text)

            // Show delete button only if it's the current user's reply
            if (reply.userId == currentUserId) {
                val deleteBtn = ImageButton(context).apply {
                    setImageResource(android.R.drawable.ic_menu_delete)
                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                }

                deleteBtn.setOnClickListener {
                    AlertDialog.Builder(context)
                        .setTitle("Delete Reply")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes") { _, _ ->
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

    private fun deleteReply(
        postId: Int,
        replyId: Int,
        holder: PostViewHolder,
        context: Context
    ) {
        RetrofitInstance.api.deleteReply(postId, replyId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Reply deleted", Toast.LENGTH_SHORT).show()
                    // Refresh replies for this post
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
        RetrofitInstance.api.sendReply(postId, CreateReplyRequest(userId, content))
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Reply sent", Toast.LENGTH_SHORT).show()
                        // Automatically show/refresh replies
                        if (holder.repliesLayout.visibility == View.GONE) {
                            holder.btnShowReplies.performClick()
                        } else {
                            // If already open, just fetch again to show the new one
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

    private fun getTimeAgo(timestamp: String): String {
        return try {
            val f = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            f.timeZone = TimeZone.getTimeZone("UTC")
            val time = f.parse(timestamp)?.time ?: return timestamp
            val diff = System.currentTimeMillis() - time
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
