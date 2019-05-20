package com.hamz4k.bestposts.domain.posts

data class PostOverview(
    val userId: Int,
    val id: Int,
    val avatarUrl: String,
    val title: String,
    val body: String
)



