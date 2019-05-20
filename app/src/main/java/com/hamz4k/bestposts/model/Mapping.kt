package com.hamz4k.bestposts.model

import com.hamz4k.bestposts.domain.comment.Comment
import com.hamz4k.bestposts.domain.posts.detail.Post
import com.hamz4k.bestposts.domain.posts.PostOverview

fun PostUi.toPost() = PostOverview(
    userId = userId,
    id = id,
    avatarUrl = avatarUrl,
    title = title,
    body = body
)

fun PostOverview.toUi() = PostUi(
    userId = userId,
    id = id,
    avatarUrl = avatarUrl,
    title = title,
    body = body
)

fun Post.toDetail() = PostDetailItem.Detail(
    author = user.name,
    avatarUrl = user.avatarUrl,
    title = title,
    body = body
)

fun Comment.toCommentItem() = PostDetailItem.Comment(
    id = id,
    name = name,
    email = email,
    body = body
)