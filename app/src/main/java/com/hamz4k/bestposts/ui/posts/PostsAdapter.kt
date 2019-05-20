package com.hamz4k.bestposts.ui.posts

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hamz4k.bestposts.R
import com.hamz4k.bestposts.domain.posts.PostOverview
import com.hamz4k.bestposts.utils.inflate

class PostsAdapter(private val postClickListener: (PostOverview) -> Unit) :
    ListAdapter<PostOverview, PostsAdapter.PostViewHolder>(PostDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(parent.inflate(R.layout.post_list_item))
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position), postClickListener)
    }


    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val titleView: TextView = itemView.findViewById(R.id.post_list_item_title)
        private val bodySnippetView: TextView =
            itemView.findViewById(R.id.post_list_item_body_snippet)
        private val userView: ImageView = itemView.findViewById(R.id.post_list_item_user_image)

        @SuppressLint("SetTextI18n")
        fun bind(item: PostOverview, postClickListener: (PostOverview) -> Unit) {

            titleView.text = item.title
            bodySnippetView.text = item.body

            Glide.with(itemView.context)
                .load(item.avatarUrl)
                .into(userView)

            itemView.setOnClickListener {
                postClickListener.invoke(item)
            }
        }
    }

    class PostDiffCallback : DiffUtil.ItemCallback<PostOverview>() {
        override fun areItemsTheSame(oldItem: PostOverview, newItem: PostOverview): Boolean = true

        override fun areContentsTheSame(oldItem: PostOverview, newItem: PostOverview): Boolean {
            return oldItem.id == newItem.id
        }
    }
}