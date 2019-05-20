package com.hamz4k.bestposts.data.comment.repository

import com.hamz4k.bestposts.data.service.BestPostsService
import com.hamz4k.bestposts.data.toComment
import com.hamz4k.bestposts.domain.comment.CommentRepository
import com.hamz4k.bestposts.domain.comment.Comment
import io.reactivex.Observable
import javax.inject.Inject

class NetworkCommentRepository @Inject constructor(private val service: BestPostsService) :
    CommentRepository {

    override fun fetchComments(postId: Int): Observable<List<Comment>> {
        return service.comments(postId).map { comment -> comment.map { it.toComment() } }
    }
}