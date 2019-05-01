package com.hamz4k.bestposts.di

import com.hamz4k.data.posts.NetworkPostsRepository
import com.hamz4k.domain.posts.PostsRepository
import dagger.Binds
import dagger.Module

@Module
abstract class RemoteModule {

    @Binds
    abstract fun bindPostsRepository(postsRepository: NetworkPostsRepository): PostsRepository
}