package com.hamz4k.bestposts.ui.postdetail

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
import com.hamz4k.bestposts.presentation.PostDetailItem
import com.hamz4k.bestposts.utils.inflate

class DetailAdapter : ListAdapter<PostDetailItem, RecyclerView.ViewHolder>(DetailDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.id.post_detail_list_comment_header -> CommentHeaderViewHolder((parent.inflate(R.layout.detail_comment_header_list_item)))
            R.id.post_detail_list_comment -> CommentViewHolder((parent.inflate(R.layout.detail_comment_list_item)))
            R.id.post_detail_list_detail -> DetailViewHolder(parent.inflate(R.layout.detail_post_list_item))
            else -> throw IllegalStateException("Item type should be comment, header or detail")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CommentViewHolder -> holder.bind(getItem(position) as PostDetailItem.Comment)
            is DetailViewHolder -> holder.bind(getItem(position) as PostDetailItem.Detail)
            is CommentHeaderViewHolder -> holder.bind(getItem(position) as PostDetailItem.CommentHeader)
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> R.id.post_detail_list_detail
            1 -> R.id.post_detail_list_comment_header
            else -> R.id.post_detail_list_comment
        }
    }

    class DetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val authorImageView: ImageView = itemView.findViewById(R.id.post_detail_list_item_avatar)
        private val titleView: TextView = itemView.findViewById(R.id.post_detail_list_item_title)
        private val bodyView: TextView = itemView.findViewById(R.id.post_detail_list_item_body)
        private val authorView: TextView = itemView.findViewById(R.id.post_detail_list_item_author)

        @SuppressLint("SetTextI18n")
        fun bind(item: PostDetailItem.Detail) {
            Glide.with(itemView.context)
                .load(item.avatarUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(authorImageView)

            titleView.text = item.title
            bodyView.text = item.body
            authorView.text = item.author
        }
    }

    class CommentHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val commentHeaderView: TextView = itemView.findViewById(R.id.detail_comment_list_item_title)

        @SuppressLint("SetTextI18n")
        fun bind(item: PostDetailItem.CommentHeader) {
            commentHeaderView.text = itemView.context.getString(R.string.comment_header_count, item.count)
        }
    }

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val authorView: TextView = itemView.findViewById(R.id.detail_comment_list_item_author)
        private val titleView: TextView = itemView.findViewById(R.id.detail_comment_list_item_title)
        private val bodyView: TextView = itemView.findViewById(R.id.detail_comment_list_item_body)

        @SuppressLint("SetTextI18n")
        fun bind(item: PostDetailItem.Comment) {
            authorView.text = item.email
            titleView.text = item.title
            bodyView.text = item.body
        }
    }

    class DetailDiffCallback : DiffUtil.ItemCallback<PostDetailItem>() {
        override fun areItemsTheSame(oldItem: PostDetailItem, newItem: PostDetailItem): Boolean {
            return oldItem::class == newItem::class
        }

        override fun areContentsTheSame(oldItem: PostDetailItem, newItem: PostDetailItem): Boolean {
            if (oldItem is PostDetailItem.Detail && newItem is PostDetailItem.Detail) {
                return true
            }
            if (oldItem is PostDetailItem.CommentHeader && newItem is PostDetailItem.CommentHeader) {
                return true
            }
            return (oldItem as? PostDetailItem.Comment)?.let {
                it.id == (newItem as? PostDetailItem.Comment)?.id
            } ?: false
        }
    }

}