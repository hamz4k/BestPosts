package com.hamz4k.bestposts.di

import com.hamz4k.bestposts.data.comment.repository.NetworkCommentRepository
import com.hamz4k.bestposts.data.posts.repository.NetworkPostsRepository
import com.hamz4k.bestposts.data.user.repository.NetworkUserRepository
import com.hamz4k.bestposts.domain.comment.CommentRepository
import com.hamz4k.bestposts.domain.posts.PostsRepository
import com.hamz4k.bestposts.domain.user.UserRepository
import dagger.Binds
import dagger.Module

@Module
abstract class RemoteModule {

    @Binds
    abstract fun bindPostsRepository(postsRepository: NetworkPostsRepository): PostsRepository
    @Binds
    abstract fun bindUserRepository(postsRepository: NetworkUserRepository): UserRepository
    @Binds
    abstract fun bindCommentRepository(postsRepository: NetworkCommentRepository): CommentRepository
}