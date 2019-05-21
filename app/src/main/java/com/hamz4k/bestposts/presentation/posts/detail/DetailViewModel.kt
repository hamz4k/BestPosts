package com.hamz4k.bestposts.presentation.posts.detail

import com.hamz4k.bestposts.R
import com.hamz4k.bestposts.domain.RxSchedulers
import com.hamz4k.bestposts.domain.posts.detail.Post
import com.hamz4k.bestposts.domain.posts.detail.PostDetailUseCase
import com.hamz4k.bestposts.model.*
import com.hamz4k.bestposts.presentation.BaseViewModel
import com.hamz4k.bestposts.presentation.State
import com.hamz4k.bestposts.presentation.posts.detail.ResultDetailEvent.DetailLoadedResult
import com.hamz4k.bestposts.presentation.posts.detail.ResultDetailEvent.LoadingFailedResult
import com.hamz4k.bestposts.presentation.toError
import com.hamz4k.bestposts.presentation.toSuccess
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import timber.log.Timber
import javax.inject.Inject

class DetailViewModelFactory @Inject constructor(private val useCase: PostDetailUseCase) {
    fun supply() = DetailViewModel(postsRepository = useCase)
}

class DetailViewModel(private val postsRepository: PostDetailUseCase) :
    BaseViewModel<DetailEvents, DetailViewState, ResultDetailEvent, DetailViewEffect>() {

    /* ***************** */
    /*     Life cycle    */
    /* ***************** */

    init {
        setupViewEventsToViewChangesTransformation()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelDisposable?.dispose()
    }

    /* ****************** */
    /*   Transformations  */
    /* ****************** */

    override fun setupViewEventsToViewChangesTransformation() {
        val viewChanges = viewEventsObservable
            .doOnNext { Timber.d("==== event received ${it.javaClass.simpleName}") }
            .compose(eventToResult())
            .doOnNext { Timber.d("==== transformed to result ${it.javaClass.simpleName}") }
            .publish()

        viewChanges.compose(resultToState()).subscribe(viewState)
        viewChanges.compose(resultToEffect()).subscribe(viewEffects)

        viewChanges.autoConnect(0) { viewModelDisposable = it }
    }

    override fun eventToResult(): ObservableTransformer<DetailEvents, State<out ResultDetailEvent>> {
        return ObservableTransformer { upstream ->
            upstream.publish { eventsObservable ->
                Observable.merge(
                    eventsObservable.ofType(DetailEvents.ScreenLoad::class.java)
                        .compose(onScreenLoad()),
                    eventsObservable.ofType(DetailEvents.Retry::class.java)
                        .compose(onRetry())
                )
            }
        }
    }

    override fun resultToEffect(): ObservableTransformer<State<out ResultDetailEvent>, DetailViewEffect> {
        //No view effects
        return ObservableTransformer { Observable.empty() }
    }

    /* ****************** */
    /*       Reducer      */
    /* ****************** */

    override fun resultToState(): ObservableTransformer<State<out ResultDetailEvent>, DetailViewState> {
        return ObservableTransformer { upstream ->
            upstream.scan(viewState.value ?: DetailViewState()) { viewState, state ->
                when (state) {
                    is State.Loading -> viewState.copy(detail = emptyList(),
                                                       isLoading = true,
                                                       error = null)
                    is State.Success -> when (state.packet) {
                        is DetailLoadedResult ->
                            viewState.copy(detail = state.packet.detailList,
                                           isLoading = false,
                                           error = null)
                        else -> viewState
                    }
                    is State.Error -> {
                        val failureState = (state.packet as LoadingFailedResult)
                        viewState.copy(detail = emptyList(),
                                       isLoading = false,
                                       error = R.string.error_message)
                            .also {
                                Timber.e(failureState.throwable,
                                         "something went wrong observing view state")
                            }
                    }
                }
            }
                .distinctUntilChanged()
                .doOnError { t: Throwable ->
                    Timber.e(t, "something went wrong observing view state")
                    viewEffects.onNext(DetailViewEffect.DisplayErrorSnackbar(R.string.error_message))
                }
                .onErrorResumeNext(Observable.empty())
        }
    }

    /* ****************** */
    /*      Use cases     */
    /* ****************** */

    private fun onScreenLoad(): ObservableTransformer<DetailEvents.ScreenLoad,
            State<out ResultDetailEvent>> {
        return ObservableTransformer { upstream ->
            upstream.switchMap { screenLoad ->
                viewState.value?.takeIf { it.detail.isNotEmpty() }
                    ?.let { Observable.just(it.detail.toSuccessResult()) }
                    ?: fetchDetails(screenLoad.post)
            }
        }
    }

    private fun onRetry(): ObservableTransformer<DetailEvents.Retry, State<ResultDetailEvent>> {
        return ObservableTransformer { upstream ->
            upstream.switchMap { retry -> fetchDetails(retry.post) }
        }
    }

    /* ******************* */
    /*     Side effects    */
    /* ******************* */

    private fun fetchDetails(post: UiPostOverview): Observable<State<ResultDetailEvent>> {
        return postsRepository.postDetail(post.toPost())
            .subscribeOn(RxSchedulers.io())
            .map { it.toSuccessResult() }
            .onErrorResumeNext { t: Throwable ->
                Observable.just(t.toDetailEventResult())
            }
            .startWith(State.Loading())
    }
}

fun Post.toSuccessResult(): State<ResultDetailEvent> {
    return (DetailLoadedResult(
        detailList = mutableListOf(toDetail(),
                                   UiPostDetailItem.CommentHeader(comments.size)
        ).plus(comments.map { it.toCommentItem() }).toList()
    ) as ResultDetailEvent).toSuccess()
}

fun Collection<UiPostDetailItem>.toSuccessResult() =
    (DetailLoadedResult(detailList = toList()) as ResultDetailEvent).toSuccess()

fun Throwable.toDetailEventResult() =
    (LoadingFailedResult(this) as ResultDetailEvent).toError()
