package com.hamz4k.bestposts.presentation.posts

import androidx.annotation.StringRes
import com.hamz4k.bestposts.domain.posts.PostOverview

data class PostsViewState(
    val posts: List<PostOverview> = emptyList(),
    val isLoading: Boolean = false,
    @StringRes val error: Int? = null)

sealed class PostsViewEffect {
    data class NavigateToPostDetail(val post: PostOverview) : PostsViewEffect()
    data class DisplayErrorSnackbar(@StringRes val message: Int) : PostsViewEffect()
}

sealed class PostsEvents {
    object ScreenLoad : PostsEvents()
    object Retry : PostsEvents()
    data class PostClicked(val post: PostOverview) : PostsEvents()

}

sealed class ResultPostsEvent {
    data class NavigateToDetail(val post: PostOverview) : ResultPostsEvent()
    data class PostsLoadedPostsEvent(val posts: List<PostOverview>) : ResultPostsEvent()
    data class LoadingFailed(val throwable: Throwable) : ResultPostsEvent()
}
