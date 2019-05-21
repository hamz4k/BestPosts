package com.hamz4k.bestposts.ui.posts.detail

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
import com.hamz4k.bestposts.model.UiPostDetailItem
import com.hamz4k.bestposts.utils.inflate

class DetailAdapter : ListAdapter<UiPostDetailItem, RecyclerView.ViewHolder>(DetailDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.id.post_detail_list_comment_header -> CommentHeaderViewHolder(
                (parent.inflate(R.layout.detail_comment_header_list_item)))
            R.id.post_detail_list_comment -> CommentViewHolder(
                (parent.inflate(R.layout.detail_comment_list_item)))
            R.id.post_detail_list_detail -> DetailViewHolder(
                parent.inflate(R.layout.detail_post_list_item))
            else -> throw IllegalStateException("Item type should be comment, header or detail")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CommentViewHolder -> holder.bind(getItem(position) as UiPostDetailItem.Comment)
            is DetailViewHolder -> holder.bind(getItem(position) as UiPostDetailItem.Detail)
            is CommentHeaderViewHolder -> holder.bind(getItem(position) as UiPostDetailItem.CommentHeader)
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

        private val authorImageView: ImageView =
            itemView.findViewById(R.id.post_detail_list_item_avatar)
        private val titleView: TextView = itemView.findViewById(R.id.post_detail_list_item_title)
        private val bodyView: TextView = itemView.findViewById(R.id.post_detail_list_item_body)
        private val authorView: TextView = itemView.findViewById(R.id.post_detail_list_item_author)

        @SuppressLint("SetTextI18n")
        fun bind(item: UiPostDetailItem.Detail) {
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

        private val commentHeaderView: TextView =
            itemView.findViewById(R.id.detail_comment_list_item_author_name)

        @SuppressLint("SetTextI18n")
        fun bind(item: UiPostDetailItem.CommentHeader) {
            commentHeaderView.text =
                itemView.context.getString(R.string.comment_header_count, item.count)
        }
    }

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val authorEmailView: TextView =
            itemView.findViewById(R.id.detail_comment_list_item_author_email)
        private val authorNameView: TextView = itemView.findViewById(R.id.detail_comment_list_item_author_name)
        private val bodyView: TextView = itemView.findViewById(R.id.detail_comment_list_item_body)

        @SuppressLint("SetTextI18n")
        fun bind(item: UiPostDetailItem.Comment) {
            authorEmailView.text = item.email
            authorNameView.text = item.name
            bodyView.text = item.body
        }
    }

    class DetailDiffCallback : DiffUtil.ItemCallback<UiPostDetailItem>() {
        override fun areItemsTheSame(oldItem: UiPostDetailItem,
                                     newItem: UiPostDetailItem): Boolean {
            return oldItem::class == newItem::class
        }

        override fun areContentsTheSame(oldItem: UiPostDetailItem,
                                        newItem: UiPostDetailItem): Boolean {
            if (oldItem is UiPostDetailItem.Detail && newItem is UiPostDetailItem.Detail) {
                return true
            }
            if (oldItem is UiPostDetailItem.CommentHeader && newItem is UiPostDetailItem.CommentHeader) {
                return true
            }
            return (oldItem as? UiPostDetailItem.Comment)?.let {
                it.id == (newItem as? UiPostDetailItem.Comment)?.id
            } ?: false
        }
    }

}