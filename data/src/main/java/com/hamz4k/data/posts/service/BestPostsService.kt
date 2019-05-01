package com.hamz4k.data.posts.service

import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import retrofit2.http.GET

interface BestPostsService {
    @GET("posts")
    fun posts(): Observable<List<PostData>>
}


data class PostData(
    @SerializedName("userId") val userId: Int,
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String
)