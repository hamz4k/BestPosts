package com.hamz4k.bestposts.domain.comment

import io.reactivex.Observable

interface CommentRepository {

    fun fetchComments(postId: Int): Observable<List<Comment>>
}