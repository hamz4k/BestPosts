package com.hamz4k.bestposts.model

sealed class State<T> {
    class Loading<T> : State<T>()
    data class Success<T>(val packet: T) : State<T>()
    data class Error<T>(val packet: T) : State<T>()
}

fun <T> T.toSuccess() = State.Success(this) as State<T>
fun <T> T.toError() = State.Error(this) as State<T>