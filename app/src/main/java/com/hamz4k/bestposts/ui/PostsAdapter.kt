package com.hamz4k.bestposts.ui

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hamz4k.bestposts.R
import com.hamz4k.bestposts.utils.growShrink
import com.hamz4k.bestposts.utils.inflate
import com.hamz4k.domain.posts.model.Post

class PostsAdapter(private val postClickListener: (Post) -> Unit) :
    ListAdapter<Post, PostsAdapter.PostViewHolder>(PostDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(parent.inflate(R.layout.post_list_item))
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position), postClickListener)
    }


    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val titleView: TextView = itemView.findViewById(R.id.post_list_item_title)
        private val bodySnippetView: TextView = itemView.findViewById(R.id.post_list_item_body_snippet)
        private val userView: ImageView = itemView.findViewById(R.id.post_list_item_user_image)

        @SuppressLint("SetTextI18n")
        fun bind(item: Post, postClickListener: (Post) -> Unit) {

            titleView.text = item.title
            bodySnippetView.text = item.body

            Glide.with(itemView.context)
                .load(item.avatarUrl)
                .apply(RequestOptions.circleCropTransform())
//                .placeholder(spinner)
                .into(userView)

            itemView.setOnClickListener {
                postClickListener.invoke(item)
                userView.growShrink()
            }
        }
    }

    class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean = true

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }
    }
}