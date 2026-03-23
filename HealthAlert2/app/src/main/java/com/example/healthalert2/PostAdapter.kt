package com.example.healthalert2

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(private var postList: MutableList<Post>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        val tvTimestamp: TextView = itemView.findViewById(R.id.tvTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]

        holder.tvTitle.text = post.title
        holder.tvContent.text = post.content
        holder.tvTimestamp.text = post.timestamp

        // HOLD CLICK ON A POST ALLOWS TO EDIT
        holder.itemView.setOnLongClickListener {
            val intent = Intent(holder.itemView.context, CreatePostActivity::class.java)

            intent.putExtra("postId", post.postId)
            intent.putExtra("title", post.title)
            intent.putExtra("content", post.content)

            holder.itemView.context.startActivity(intent)

            true
        }
    }

    override fun getItemCount(): Int = postList.size

    fun updatePosts(newPosts: List<Post>) {
        postList.clear()
        postList.addAll(newPosts)
        notifyDataSetChanged()
    }

    fun addPost(post: Post) {
        postList.add(0, post)
        notifyItemInserted(0)
    }
}