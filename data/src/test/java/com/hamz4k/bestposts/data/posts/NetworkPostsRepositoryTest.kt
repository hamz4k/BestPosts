package com.hamz4k.bestposts.data.posts

import com.hamz4k.bestposts.data.model.Fakes
import com.hamz4k.bestposts.data.posts.repository.NetworkPostsRepository
import com.hamz4k.bestposts.data.service.BestPostsService
import com.hamz4k.bestposts.domain.RxSchedulers
import com.hamz4k.bestposts.domain.initForTests
import com.nhaarman.mockito_kotlin.given
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class NetworkPostsRepositoryTest {
    private val fakes = Fakes()
    private val fakesData = fakes.data
    private val fakesDomain = fakes.domain

    private val postDataList = fakesData.postList
    private val postList = fakesDomain.postList

    @Mock
    lateinit var mockService: BestPostsService
    private lateinit var postsRepository: NetworkPostsRepository

    @Before
    fun setUp() {
        RxSchedulers.initForTests()
        MockitoAnnotations.initMocks(this)

        postsRepository = NetworkPostsRepository(mockService)
    }

    @Test
    fun should_map_post_data_to_post() {
        //given
        given(mockService.posts()).willReturn(Observable.just(postDataList))
        //when
        val observer = postsRepository.fetchPosts().test()
        //then
        observer.assertValueCount(1)
            .assertNoErrors()
            .assertValue { posts -> posts == postList }
    }

}