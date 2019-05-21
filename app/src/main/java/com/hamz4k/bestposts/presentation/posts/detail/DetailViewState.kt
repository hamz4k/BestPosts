package com.hamz4k.bestposts.presentation.posts.detail

import androidx.annotation.StringRes
import com.hamz4k.bestposts.model.UiPostDetailItem
import com.hamz4k.bestposts.model.UiPostOverview

data class DetailViewState(
    val detail: List<UiPostDetailItem> = emptyList(),
    val isLoading: Boolean = false,
    @StringRes val error: Int? = null
)

sealed class DetailViewEffect {
    data class DisplayErrorSnackbar(@StringRes val message: Int) : DetailViewEffect()
}

sealed class DetailEvents {
    data class ScreenLoad(val post: UiPostOverview) : DetailEvents()
    data class Retry(val post: UiPostOverview) : DetailEvents()
}

sealed class ResultDetailEvent {
    data class DetailLoadedResult(val detailList: List<UiPostDetailItem>) :
        ResultDetailEvent()

    data class LoadingFailedResult(val throwable: Throwable) : ResultDetailEvent()
}