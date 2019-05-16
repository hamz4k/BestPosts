package com.hamz4k.data.posts.service

import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BestPostsService {
    @GET("posts")
    fun posts(): Observable<List<PostData>>

    @GET("comments")
    fun comments(@Query("postId") postId: Int): Observable<List<CommentData>>

    @GET("users/{userId}")
    fun user(@Path("userId") userId: Int): Observable<UserData>
}


data class PostData(
    @SerializedName("userId") val userId: Int,
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String
)

data class UserData(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("website") val website: String
)

data class CommentData(
    @SerializedName("id") val id: Int,
    @SerializedName("postId") val postIdr: Int,
    @SerializedName("name") val title: String,
    @SerializedName("email") val email: String,
    @SerializedName("body") val body: String
)
