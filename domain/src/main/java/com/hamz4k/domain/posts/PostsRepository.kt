package com.hamz4k.domain.posts

import com.hamz4k.domain.posts.model.Post
import io.reactivex.Observable

interface PostsRepository {

    fun fetchPosts(): Observable<List<Post>>

}