package com.hamz4k.bestposts.data.utils

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.rules.ExternalResource

class MockServer : ExternalResource() {

    private val server = MockWebServer().apply {
        setDispatcher(DefaultTestDispatcher())
    }

    val baseUrl: String
        get() = server.url("/").toString()

    private fun start() {
        server.start()
    }

    private fun stop() {
        server.shutdown()
    }

    override fun before() {
        start()
    }

    override fun after() {
        stop()
    }

    fun setDispatcher(f: (RecordedRequest) -> MockResponse) {
        server.setDispatcher(object : Dispatcher() {
            override fun dispatch(request: RecordedRequest) = f(request)
        })
    }

    class DefaultTestDispatcher : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            return MockResponse().setResponseCode(200)
        }
    }
}