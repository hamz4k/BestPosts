package com.hamz4k.bestposts.domain.comment

data class Comment(
    val id: Int,
    val postId: Int,
    val name: String,
    val email: String,
    val body: String
)