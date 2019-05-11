package com.hamz4k.bestposts.model

import android.os.Parcelable
import com.hamz4k.domain.posts.model.PostLight
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostUi(
    val userId: Int,
    val id: Int,
    val avatarUrl: String,
    val title: String,
    val body: String
) : Parcelable


fun PostLight.toUi() = PostUi(
    userId = userId,
    id = id,
    avatarUrl = avatarUrl,
    title = title,
    body = body
)

