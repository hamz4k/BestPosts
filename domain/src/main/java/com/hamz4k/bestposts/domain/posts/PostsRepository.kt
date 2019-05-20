package com.hamz4k.bestposts.domain.posts

import io.reactivex.Observable

interface PostsRepository {

    fun fetchPosts(): Observable<List<PostOverview>>

}