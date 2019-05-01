package com.hamz4k.data.posts

import com.hamz4k.data.posts.service.BestPostsService
import com.hamz4k.data.posts.service.PostData
import com.hamz4k.domain.posts.PostsRepository
import com.hamz4k.domain.posts.model.Post
import io.reactivex.Observable
import javax.inject.Inject

class NetworkPostsRepository @Inject constructor(private val postsService: BestPostsService) : PostsRepository {

    override fun fetchPosts(): Observable<List<Post>> {
        return postsService.posts().map { postList -> postList.map { it.toPost() } }
    }
}


fun PostData.toPost() = Post(
    userId = userId,
    id = id,
    avatarUrl = "https://api.adorable.io/avatars/64/$userId@adorable",
    title = title,
    body = body
)