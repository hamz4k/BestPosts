package com.hamz4k.bestposts.model

import androidx.annotation.StringRes

data class DetailViewState(
    val detail: List<PostDetailItem> = emptyList(),
    val isLoading: Boolean = false,
    @StringRes val error: Int? = null
)

sealed class DetailViewEffect {
    data class DisplayErrorSnackbar(@StringRes val message: Int) : DetailViewEffect()
}

sealed class DetailEvents {
    data class ScreenLoad(val post: PostUi) : DetailEvents()
    data class Retry(val post: PostUi) : DetailEvents()
}

sealed class ResultDetailEvent {
    data class DetailLoadedResult(val detailList: List<PostDetailItem>) :
        ResultDetailEvent()

    data class LoadingFailedResult(val throwable: Throwable) : ResultDetailEvent()
}