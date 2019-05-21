package com.hamz4k.bestposts.model

sealed class UiPostDetailItem {
    data class Detail(
        val author: String,
        val avatarUrl: String,
        val title: String,
        val body: String) : UiPostDetailItem()

    data class CommentHeader(val count: Int) : UiPostDetailItem()

    data class Comment(
        val id: Int,
        val name: String,
        val email: String,
        val body: String) : UiPostDetailItem()
}