package com.hamz4k.bestposts.data.comment.model

import com.google.gson.annotations.SerializedName

data class CommentData(
    @SerializedName("id") val id: Int,
    @SerializedName("postId") val postId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("body") val body: String
)