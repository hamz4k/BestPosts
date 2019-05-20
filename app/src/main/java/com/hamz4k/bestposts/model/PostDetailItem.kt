package com.hamz4k.bestposts.model
    sealed class PostDetailItem {
        data class Detail(
            val author: String,
            val avatarUrl: String,
            val title: String,
            val body: String

        ) : PostDetailItem()

        data class CommentHeader(val count: Int) : PostDetailItem()

        data class Comment(
            val id: Int,
            val name: String,
            val email: String,
            val body: String
        ) : PostDetailItem()
    }