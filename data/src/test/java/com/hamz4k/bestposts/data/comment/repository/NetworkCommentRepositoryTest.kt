package com.hamz4k.bestposts.data.comment.repository

import com.hamz4k.bestposts.data.service.BestPostsService
import com.hamz4k.bestposts.domain.RxSchedulers
import com.hamz4k.bestposts.domain.initForTests
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.given
import com.hamz4k.bestposts.data.model.Fakes
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class NetworkCommentRepositoryTest {

    @Mock
    lateinit var service: BestPostsService
    private lateinit var commentRepository: NetworkCommentRepository

    private val fakes = Fakes()
    private val commentsData = fakes.data.comments
    private val comments = fakes.domain.comments

    @Before
    fun setup() {
        RxSchedulers.initForTests()
        MockitoAnnotations.initMocks(this)
        commentRepository = NetworkCommentRepository(service)
    }

    @Test
    fun should_map_comment_data_to_comment() {
        //given
        val postId = 1
        given(service.comments(eq(postId))).willReturn(Observable.just(commentsData))
        //when
        val observer = commentRepository.fetchComments(postId).test()
        //then
        observer.assertValueCount(1)
            .assertNoErrors()
            .assertValue { result -> result == comments }
    }


}