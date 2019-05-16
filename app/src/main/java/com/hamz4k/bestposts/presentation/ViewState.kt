package com.hamz4k.bestposts.presentation

import com.hamz4k.domain.posts.model.PostLight

sealed class State<T> {
    class Loading<T> : State<T>()
    data class Success<T>(val packet: T) : State<T>()
    data class Error<T>(val packet: T) : State<T>()
}

sealed class PostsEventResult {
    data class NavigateToDetail(val post: PostLight) : PostsEventResult()
    data class PostsLoaded(val posts: List<PostLight>) : PostsEventResult()
    data class LoadingFailed(val throwable: Throwable) : PostsEventResult()
}


sealed class PostsEvents {
    object ScreenLoad : PostsEvents()
    object Retry : PostsEvents()
    data class PostClicked(val post: PostLight) : PostsEvents()
}

data class PostsViewState(
    val posts: List<PostLight> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null)

sealed class PostsViewEffect {
    data class NavigateToPostDetail(val post: PostLight) : PostsViewEffect()
}


fun Collection<PostLight>.toLoaded() = PostsEventResult.PostsLoaded(posts = toList()) as PostsEventResult
fun Throwable.toFailed() = PostsEventResult.LoadingFailed(this) as PostsEventResult
fun <T> T.toSuccess() = State.Success(this) as State<T>
fun <T> T.toError() = State.Error(this) as State<T>
//    fun State.Success<PostsEventResult>.toPosts() = (packet as PostsEventResult.PostsLoaded).posts
//    fun State.Error<PostsEventResult>.toError() = (packet as PostsEventResult.LoadingFailed).throwable