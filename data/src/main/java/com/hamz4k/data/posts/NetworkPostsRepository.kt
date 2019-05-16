package com.hamz4k.data.posts

import com.hamz4k.data.posts.service.BestPostsService
import com.hamz4k.data.posts.service.CommentData
import com.hamz4k.data.posts.service.UserData
import com.hamz4k.domain.posts.PostsRepository
import com.hamz4k.domain.posts.model.Post
import com.hamz4k.domain.posts.model.PostLight
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import javax.inject.Inject

class NetworkPostsRepository @Inject constructor(private val postsService: BestPostsService) :
    PostsRepository {

    override fun fetchPostDetails(post: PostLight): Observable<Post> {

        val postId = post.id
        val userId = post.userId

        return Observable.zip(postsService.user(userId),
                              postsService.comments(postId),
                              BiFunction
                              { user: UserData, comments: List<CommentData> ->
                                  Post(
                                      id = 5,
                                      user = user.toUser(),
                                      title = post.title,
                                      body = post.body,
                                      comments = comments.map { it.toComment() }

                                      )
                              }
                             )
    }

    override fun fetchPosts(): Observable<List<PostLight>> {
        return postsService.posts().map { postList -> postList.map { it.toPost() } }
    }
}