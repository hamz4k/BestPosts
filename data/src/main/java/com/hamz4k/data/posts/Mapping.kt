package com.hamz4k.data.posts

import com.hamz4k.data.posts.service.CommentData
import com.hamz4k.data.posts.service.PostData
import com.hamz4k.data.posts.service.UserData
import com.hamz4k.domain.posts.model.Comment
import com.hamz4k.domain.posts.model.PostLight
import com.hamz4k.domain.posts.model.User

fun CommentData.toComment() = Comment(
    id = id,
    postId = postIdr,
    title = title,
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
    website = website
                            )


fun PostData.toPost() = PostLight(
    userId = userId,
    id = id,
    avatarUrl = "https://api.adorable.io/avatars/112/$userId@adorable",
    title = title,
    body = body
                                 )