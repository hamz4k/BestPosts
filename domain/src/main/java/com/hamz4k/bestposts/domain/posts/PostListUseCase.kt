package com.hamz4k.bestposts.domain.posts

import io.reactivex.Observable
import javax.inject.Inject

class PostListUseCase @Inject constructor(private val postsRepository: PostsRepository) {

    fun postList(): Observable<List<PostOverview>> {
        return postsRepository.fetchPosts()
    }

}