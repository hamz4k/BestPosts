package com.hamz4k.bestposts.data

import com.hamz4k.bestposts.data.comment.model.CommentData
import com.hamz4k.bestposts.data.posts.model.PostData
import com.hamz4k.bestposts.data.user.model.UserData
import com.hamz4k.bestposts.domain.comment.Comment
import com.hamz4k.bestposts.domain.posts.PostOverview
import com.hamz4k.bestposts.domain.user.User

fun CommentData.toComment() = Comment(
    id = id,
    postId = postId,
    name = name,
    email = email,
    body = body
)

fun UserData.toUser() = User(
    id = id,
    name = name,
    username = username,
    email = email,
    avatarUrl = "https://api.adorable.io/avatars/64/$id@adorable",
    phone = phone,
    website = website)


fun PostData.toPost() = PostOverview(
    userId = userId,
    id = id,
    avatarUrl = "https://api.adorable.io/avatars/112/$userId@adorable",
    title = title,
    body = body)