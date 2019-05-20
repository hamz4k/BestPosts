package com.hamz4k.bestposts.domain.posts

import com.hamz4k.bestposts.domain.Fakes
import com.hamz4k.bestposts.domain.RxSchedulers
import com.hamz4k.bestposts.domain.initForTests
import com.nhaarman.mockito_kotlin.given
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class PostListUseCaseTest {
    @Mock
    lateinit var postRepository: PostsRepository

    private lateinit var postListUseCase: PostListUseCase

    val fakes = Fakes()

    @Before
    fun setup() {
        RxSchedulers.initForTests()
        MockitoAnnotations.initMocks(this)

        postListUseCase = PostListUseCase(postRepository)
    }

    @Test
    fun should_observe_post_overview_list_and_complete() {
        //given
        given(postRepository.fetchPosts()).willReturn(Observable.just(fakes.postOverviewList))
        //when
        val observer = postListUseCase.postList().test()
        //then
        observer.assertValueCount(1)
            .assertNoErrors()
            .assertComplete()
            .assertValue { postList -> postList.size == 9 }
            .assertValue { postList -> postList == fakes.postOverviewList }
    }
}