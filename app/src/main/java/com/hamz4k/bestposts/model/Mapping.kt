package com.hamz4k.bestposts.model

import com.hamz4k.bestposts.presentation.PostDetailViewModel.PostDetailItem
import com.hamz4k.bestposts.presentation.PostDetailViewModel.PostDetailItem.CommentHeader
import com.hamz4k.bestposts.presentation.PostDetailViewModel.PostDetailItem.Detail
import com.hamz4k.domain.posts.model.Comment
import com.hamz4k.domain.posts.model.Post
import com.hamz4k.domain.posts.model.PostLight

fun PostUi.toPost() = PostLight(
    userId = userId,
    id = id,
    avatarUrl = avatarUrl,
    title = title,
    body = body
)

fun Post.toViewState() = PostDetailViewState(
    detail = mutableListOf(
        Detail(
            author = user.name,
            avatarUrl = user.avatarUrl,
            title = title,
            body = body
        ),
        CommentHeader(comments.size)
    ).plus(comments.map { it.toCommentItem() }).toList()
)

fun Comment.toCommentItem() = PostDetailItem.Comment(
    id = id,
    title = title,
    email = email,
    body = body
)


sealed class PostDetailEvents {
    data class ScreenLoad(val post: PostUi) : PostDetailEvents()
    data class CommentsClicked(val id: Int) : PostDetailEvents()
}

data class PostDetailViewState(
    val detail: List<PostDetailItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
