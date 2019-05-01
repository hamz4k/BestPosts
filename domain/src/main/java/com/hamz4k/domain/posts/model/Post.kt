package com.hamz4k.domain.posts.model

data class Post(
    val userId: Int,
    val id: Int,
    val avatarUrl: String,
    val title: String,
    val body: String
)