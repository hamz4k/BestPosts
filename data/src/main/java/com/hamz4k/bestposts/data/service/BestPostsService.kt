package com.hamz4k.bestposts.data.service

import com.hamz4k.bestposts.data.comment.model.CommentData
import com.hamz4k.bestposts.data.posts.model.PostData
import com.hamz4k.bestposts.data.user.model.UserData
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