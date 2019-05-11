package com.hamz4k.domain.posts

import com.hamz4k.domain.posts.model.Post
import com.hamz4k.domain.posts.model.PostLight
import io.reactivex.Observable

interface PostsRepository {

    fun fetchPosts(): Observable<List<PostLight>>

    fun fetchPostDetails(post: PostLight): Observable<Post>

}