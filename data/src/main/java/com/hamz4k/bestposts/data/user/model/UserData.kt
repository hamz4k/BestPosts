package com.hamz4k.bestposts.data.user.model

import com.google.gson.annotations.SerializedName

data class UserData(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("website") val website: String
)