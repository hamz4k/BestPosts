package com.hamz4k.bestposts.model

import com.hamz4k.bestposts.domain.comment.Comment
import com.hamz4k.bestposts.domain.posts.PostOverview
import com.hamz4k.bestposts.domain.posts.detail.Post

fun UiPostOverview.toPost() = PostOverview(
    userId = userId,
    id = id,
    avatarUrl = avatarUrl,
    title = title,
    body = body
)

fun PostOverview.toUi() = UiPostOverview(
    userId = userId,
    id = id,
    avatarUrl = avatarUrl,
    title = title,
    body = body
)

fun Post.toDetail() = UiPostDetailItem.Detail(
    author = user.name,
    avatarUrl = user.avatarUrl,
    title = title,
    body = body
)

fun Comment.toCommentItem() = UiPostDetailItem.Comment(
    id = id,
    name = name,
    email = email,
    body = body
)