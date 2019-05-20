package com.hamz4k.bestposts.domain.user

import io.reactivex.Observable

interface UserRepository {

    fun fetchUser(userId: Int): Observable<User>

}