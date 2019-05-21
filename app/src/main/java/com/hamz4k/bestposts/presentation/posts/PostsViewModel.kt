package com.hamz4k.bestposts.presentation.posts

import com.hamz4k.bestposts.R
import com.hamz4k.bestposts.domain.RxSchedulers
import com.hamz4k.bestposts.domain.posts.PostListUseCase
import com.hamz4k.bestposts.domain.posts.PostOverview
import com.hamz4k.bestposts.presentation.BaseViewModel
import com.hamz4k.bestposts.presentation.State
import com.hamz4k.bestposts.presentation.toError
import com.hamz4k.bestposts.presentation.toSuccess
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import timber.log.Timber
import javax.inject.Inject

class PostsViewModelFactory @Inject constructor(private val postsRepository: PostListUseCase) {
    fun supply() =
        PostsViewModel(postListUseCase = postsRepository)
}

class PostsViewModel(private val postListUseCase: PostListUseCase) :
    BaseViewModel<PostsEvents, PostsViewState, ResultPostsEvent, PostsViewEffect>() {

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

    override fun eventToResult(): ObservableTransformer<PostsEvents, State<out ResultPostsEvent>> {
        return ObservableTransformer { upstream ->
            upstream.publish { eventsObservable ->
                Observable.merge(
                    eventsObservable.ofType(PostsEvents.ScreenLoad::class.java)
                        .compose(onScreenLoad()),
                    eventsObservable.ofType(PostsEvents.Retry::class.java)
                        .compose(onRetry()),
                    eventsObservable.ofType(PostsEvents.PostClicked::class.java)
                        .compose(onPostClicked()))
            }
        }
    }

    override fun resultToEffect(): ObservableTransformer<State<out ResultPostsEvent>, PostsViewEffect> {
        return ObservableTransformer { upstream ->
            upstream.filter { it is State.Success && it.packet is ResultPostsEvent.NavigateToDetail }
                .map<PostsViewEffect> {
                    val navigationResult =
                        (it as State.Success).packet as ResultPostsEvent.NavigateToDetail
                    PostsViewEffect.NavigateToPostDetail(navigationResult.post)
                }
        }
    }

    /* ****************** */
    /*       Reducer      */
    /* ****************** */

    override fun resultToState(): ObservableTransformer<State<out ResultPostsEvent>, PostsViewState> {
        return ObservableTransformer { upstream ->
            upstream.scan(viewState.value ?: PostsViewState()) { viewState, state ->
                when (state) {
                    is State.Loading -> viewState.copy(posts = emptyList(),
                                                       isLoading = true,
                                                       error = null)
                    is State.Success -> when (state.packet) {
                        is ResultPostsEvent.PostsLoadedPostsEvent -> viewState.copy(posts = state.packet.posts,
                                                                                    isLoading = false,
                                                                                    error = null)
                        else -> viewState
                    }
                    is State.Error -> {
                        val failureState = (state.packet as ResultPostsEvent.LoadingFailed)
                        viewState.copy(posts = emptyList(),
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
                    viewEffects.onNext(PostsViewEffect.DisplayErrorSnackbar(R.string.error_message))
                }
                .onErrorResumeNext(Observable.empty())
        }
    }

    /* ****************** */
    /*      Use cases     */
    /* ****************** */

    private fun onPostClicked(): ObservableTransformer<PostsEvents.PostClicked, State<ResultPostsEvent.NavigateToDetail>> {
        return ObservableTransformer { upstream ->
            upstream.map<State<ResultPostsEvent.NavigateToDetail>> {
                ResultPostsEvent.NavigateToDetail(it.post).toSuccess()
            }
        }
    }

    private fun onScreenLoad(): ObservableTransformer<PostsEvents.ScreenLoad, State<ResultPostsEvent>> {
        return ObservableTransformer { upstream ->
            upstream.switchMap {
                viewState.value?.takeIf { it.posts.isNotEmpty() }
                    ?.let { Observable.just(it.posts.toSuccessResult()) }
                    ?: fetchPosts()
            }
        }
    }

    private fun onRetry(): ObservableTransformer<PostsEvents.Retry, State<ResultPostsEvent>> {
        return ObservableTransformer { upstream ->
            upstream.switchMap { fetchPosts() }
        }
    }

    /* ******************* */
    /*     Side effects    */
    /* ******************* */

    private fun fetchPosts(): Observable<State<ResultPostsEvent>> {
        return postListUseCase.postList()
            .subscribeOn(RxSchedulers.io())
            .map {
                it.toSuccessResult()
            }
            .onErrorResumeNext { t: Throwable ->
                Observable.just(t.toPostEventErrorResult())
            }
            .startWith(State.Loading())
    }
}

internal fun Collection<PostOverview>.toSuccessResult() =
    (ResultPostsEvent.PostsLoadedPostsEvent(posts = toList()) as ResultPostsEvent).toSuccess()

internal fun Throwable.toPostEventErrorResult() =
    (ResultPostsEvent.LoadingFailed(this) as ResultPostsEvent).toError()