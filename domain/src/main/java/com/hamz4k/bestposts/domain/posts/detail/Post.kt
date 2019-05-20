package com.hamz4k.bestposts.domain.posts.detail

import com.hamz4k.bestposts.domain.comment.Comment
import com.hamz4k.bestposts.domain.user.User

data class Post(
    val id: Int,
    val user: User,
    val title: String,
    val body: String,
    val comments: List<Comment>
)