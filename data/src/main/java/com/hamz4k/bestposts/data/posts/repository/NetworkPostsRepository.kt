package com.hamz4k.bestposts.data.posts.repository

import com.hamz4k.bestposts.data.service.BestPostsService
import com.hamz4k.bestposts.data.toPost
import com.hamz4k.bestposts.domain.posts.PostOverview
import com.hamz4k.bestposts.domain.posts.PostsRepository
import io.reactivex.Observable
import javax.inject.Inject

class NetworkPostsRepository @Inject constructor(private val postsService: BestPostsService) :
    PostsRepository {

    override fun fetchPosts(): Observable<List<PostOverview>> {
        return postsService.posts().map { postList -> postList.map { it.toPost() } }
    }

}