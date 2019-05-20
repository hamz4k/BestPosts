package com.hamz4k.bestposts.data.user.repository

import com.hamz4k.bestposts.data.toUser
import com.hamz4k.bestposts.data.service.BestPostsService
import com.hamz4k.bestposts.domain.user.User
import com.hamz4k.bestposts.domain.user.UserRepository
import io.reactivex.Observable
import javax.inject.Inject

class NetworkUserRepository @Inject constructor(private val service: BestPostsService) :
    UserRepository {

    override fun fetchUser(userId: Int): Observable<User> {
        return service.user(userId).map { it.toUser() }
    }
}