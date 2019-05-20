package com.hamz4k.bestposts.domain.user

data class User(
    val id: Int,
    val name: String,
    val username: String,
    val avatarUrl: String,
    val email: String,
    val phone: String,
    val website: String
)