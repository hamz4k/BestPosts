package com.hamz4k.bestposts.data.utils

import okhttp3.OkHttpClient
import okhttp3.OkHttpClient.Builder
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit.SECONDS

class HttpApiFactory(baseUrl: String) {

    companion object {
        const val CONNECTION_TIMEOUT = 30L
        const val READ_TIMEOUT = 30L
    }

    private object HttpClient {
        val client: OkHttpClient = Builder()
            .connectTimeout(CONNECTION_TIMEOUT, SECONDS)
            .readTimeout(READ_TIMEOUT, SECONDS)
            .build()
    }

    private val retrofit: Retrofit = Retrofit.Builder()
        .client(HttpClient.client)
        .baseUrl(baseUrl)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> createHttpAPi(service: Class<T>): T {
        return retrofit.create(service)
    }

    inline fun <reified T> createHttpAPi(): T {
        return createHttpAPi(T::class.java)
    }
}