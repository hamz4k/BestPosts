package com.hamz4k.bestposts.data.user.repository

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

class NetworkUserRepositoryTest {

    @Mock
    lateinit var service: BestPostsService
    private lateinit var userRepository: NetworkUserRepository

    private val fakes = Fakes()
    private val userData = fakes.data.user
    private val user = fakes.domain.user

    @Before
    fun setup() {
        RxSchedulers.initForTests()
        MockitoAnnotations.initMocks(this)
        userRepository = NetworkUserRepository(service)
    }

    @Test
    fun should_map_user_data_to_user() {
        //given
        val postId = 1
        given(service.user(eq(postId))).willReturn(Observable.just(userData))
        //when
        val observer = userRepository.fetchUser(postId).test()
        //then
        observer.assertValueCount(1)
            .assertNoErrors()
            .assertValue { result -> result == user }
    }
}