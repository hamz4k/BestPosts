package com.hamz4k.bestposts.di

import com.hamz4k.bestposts.BuildConfig
import com.hamz4k.bestposts.data.service.BestPostsService
import com.hamz4k.bestposts.ui.posts.PostsActivity
import com.hamz4k.bestposts.ui.posts.detail.PostDetailActivity
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.android.ContributesAndroidInjector
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
abstract class AppModule {

    @Module
    companion object {
        @Provides
        @Reusable
        @JvmStatic
        fun makeBestPostsService(): BestPostsService {
            val okHttpClient = makeOkHttpClient(
                makeLoggingInterceptor((BuildConfig.DEBUG))
            )
            val retrofit = Retrofit.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(BestPostsService::class.java)
        }


        private fun makeOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build()
        }

        private fun makeLoggingInterceptor(isDebug: Boolean): HttpLoggingInterceptor {
            val logging = HttpLoggingInterceptor()
            logging.level = if (isDebug) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
            return logging

        }

    }

    @ContributesAndroidInjector
    abstract fun contributesPostsActivity(): PostsActivity

    @ContributesAndroidInjector
    abstract fun contributesPostDetailActivity(): PostDetailActivity
}