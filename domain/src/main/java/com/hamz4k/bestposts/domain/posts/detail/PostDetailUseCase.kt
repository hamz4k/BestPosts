package com.hamz4k.bestposts.domain.posts.detail

import com.hamz4k.bestposts.domain.comment.Comment
import com.hamz4k.bestposts.domain.comment.CommentRepository
import com.hamz4k.bestposts.domain.posts.PostOverview
import com.hamz4k.bestposts.domain.user.User
import com.hamz4k.bestposts.domain.user.UserRepository
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import javax.inject.Inject

class PostDetailUseCase @Inject constructor(private val commentRepository: CommentRepository,
                                            private val userRepository: UserRepository) {

    fun postDetail(post: PostOverview): Observable<Post> {
        val postId = post.id
        val userId = post.userId

        return Observable.zip(userRepository.fetchUser(userId),
                              commentRepository.fetchComments(postId),
                              BiFunction
                              { user: User, comments: List<Comment> ->
                                  Post(
                                      id = postId,
                                      user = user,
                                      title = post.title,
                                      body = post.body,
                                      comments = comments
                                  )
                              }
        )
    }
}