package com.hamz4k.bestposts.domain.posts.detail

import com.hamz4k.bestposts.domain.Fakes
import com.hamz4k.bestposts.domain.RxSchedulers
import com.hamz4k.bestposts.domain.comment.CommentRepository
import com.hamz4k.bestposts.domain.initForTests
import com.hamz4k.bestposts.domain.user.UserRepository
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.given
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations


class PostDetailUseCaseTest {

    @Mock
    lateinit var userRepository: UserRepository
    @Mock
    lateinit var commentRepository: CommentRepository

    private lateinit var postDetailUseCase: PostDetailUseCase

    val fakes = Fakes()

    @Before
    fun setup() {
        RxSchedulers.initForTests()
        MockitoAnnotations.initMocks(this)

        postDetailUseCase = PostDetailUseCase(commentRepository, userRepository)
    }

    @Test
    fun should_combine_post_overview_comments_and_user_data_into_full_post_domain_model() {
        //given
        val postId = 1
        val userId = 1
        given(commentRepository.fetchComments(eq(postId))).willReturn(Observable.just(fakes.comments))
        given(userRepository.fetchUser(eq(userId))).willReturn(Observable.just(fakes.user))
        //when
        val observer = postDetailUseCase.postDetail(fakes.postOverview).test()
        //then
        observer.assertValueCount(1)
            .assertNoErrors()
            .assertValue { postDetail -> postDetail == fakes.post }
    }
}