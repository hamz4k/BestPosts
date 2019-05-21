package com.hamz4k.bestposts.data.service

import com.hamz4k.bestposts.data.model.Fakes
import com.hamz4k.bestposts.data.utils.HttpApiFactory
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import okio.Buffer
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class BestPostsServiceTest {

    @Rule
    @JvmField
    val mockServer = MockWebServer()

    private lateinit var httpApiFactory: HttpApiFactory
    private lateinit var service: BestPostsService

    private val fakesData = Fakes.Data()
    private val postList = fakesData.postList
    private val userData = fakesData.user
    private val commentItems = fakesData.comments

    @Before
    fun setup() {
        val url = mockServer.url("/").toString()
        httpApiFactory = HttpApiFactory(url)
        service = httpApiFactory.createHttpAPi()
    }

    @Test
    fun should_parse_posts_response() {
        //given
        mockServer.setDispatcher { request ->
            dispatchSynchronize(request = request,
                                path = "/posts",
                                fileName = "posts.json")
        }
        //when
        val observer = service.posts().test()
        //then
        observer.assertValueCount(1)
            .assertValue { response ->
                response == postList
            }
            .assertComplete()
            .assertNoErrors()
    }

    @Test
    fun should_parse_user_response() {
        //given
        mockServer.setDispatcher { request ->
            dispatchSynchronize(request = request,
                                path = "/users/",
                                fileName = "users.json")
        }
        val userId = 1
        //when
        val observer = service.user(userId).test()
        //then
        observer.assertValueCount(1)
            .assertValue { response ->
                response == userData
            }
            .assertComplete()
            .assertNoErrors()
    }

    @Test
    fun should_parse_comments_response() {
        //given
        mockServer.setDispatcher { request ->
            dispatchSynchronize(request = request,
                                path = "/comments",
                                fileName = "comments.json")
        }
        val postId = 1
        //when
        val observer = service.comments(postId).test()
        //then
        observer.assertValueCount(1)
            .assertValue { response ->
                response == commentItems
            }
            .assertComplete()
            .assertNoErrors()
    }

    private fun dispatchSynchronize(request: RecordedRequest,
                                    path: String,
                                    fileName: String): MockResponse {
        return when {
            request.path.startsWith(path) -> {
                BestPostsService::class.java.classLoader.getResourceAsStream(fileName)
                    .use { stream ->
                        MockResponse().setResponseCode(200).setBody(Buffer().readFrom(stream))
                    }
            }
            else -> MockResponse().setResponseCode(404)
        }
    }

    private fun MockWebServer.setDispatcher(f: (RecordedRequest) -> MockResponse) {
        setDispatcher(object : Dispatcher() {
            override fun dispatch(request: RecordedRequest) = f(request)
        })
    }
}