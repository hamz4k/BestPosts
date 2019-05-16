package com.hamz4k.bestposts.presentation

import androidx.lifecycle.ViewModel
import com.hamz4k.domain.RxSchedulers
import com.hamz4k.domain.posts.PostsRepository
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

class PostsViewModelFactory @Inject constructor(private val postsRepository: PostsRepository) {
    fun supply() = PostsViewModel(postsRepository = postsRepository)
}

class PostsViewModel(private val postsRepository: PostsRepository) : ViewModel(),
    InputsConsumer<PostsEvents>, ViewStateProvider<PostsViewState, PostsViewEffect> {

    private var viewModelDisposable: Disposable? = null

    private val eventsObservable = PublishSubject.create<PostsEvents>()
    private val viewState = BehaviorSubject.create<PostsViewState>()
    private val viewEffects = PublishSubject.create<PostsViewEffect>()

    /* ***************** */
    /*     Life cycle    */
    /* ***************** */

    init {
        val viewChanges = eventsObservable
            .doOnNext { Timber.d("==== event received ${it.javaClass.simpleName}") }
            .compose(eventToResult())
            .doOnNext { Timber.d("==== transformed to result ${it.javaClass.simpleName}") }
            .publish()

        viewChanges.compose(resultToState()).subscribe(viewState)
        viewChanges.compose(resultToEffect()).subscribe(viewEffects)

        viewChanges.autoConnect(0) { viewModelDisposable = it }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelDisposable?.dispose()
    }

    /* ****************** */
    /*      Use cases     */
    /* ****************** */

    private fun eventToResult(): ObservableTransformer<PostsEvents, State<out PostsEventResult>> {
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

    private fun resultToEffect(): ObservableTransformer<State<out PostsEventResult>, PostsViewEffect> {
        return ObservableTransformer { upstream ->
            upstream.filter { it is State.Success && it.packet is PostsEventResult.NavigateToDetail }
                .map<PostsViewEffect> {

                    val navigationResult =
                        (it as State.Success).packet as PostsEventResult.NavigateToDetail
                    PostsViewEffect.NavigateToPostDetail(navigationResult.post)
                }
        }
    }

    /* **************** */
    /*      Public      */
    /* **************** */

    override fun registerToInputs(vararg inputSources: Observable<out PostsEvents>): Disposable {
        return Observable.mergeArray(*inputSources)
            .subscribe(eventsObservable::onNext, ::handleInputsError)
    }

    override fun observeViewEffects(): Observable<PostsViewEffect> {
        return viewEffects
    }

    override fun observeViewState(): Observable<PostsViewState> {
        return viewState
    }

    /* ****************** */
    /*      Use cases     */
    /* ****************** */

    private fun onPostClicked(): ObservableTransformer<PostsEvents.PostClicked, State<PostsEventResult.NavigateToDetail>> {
        return ObservableTransformer { upstream ->
            upstream.map<State<PostsEventResult.NavigateToDetail>> {
                PostsEventResult.NavigateToDetail(it.post).toSuccess()
            }
        }
    }

    private fun onScreenLoad(): ObservableTransformer<PostsEvents.ScreenLoad, State<PostsEventResult>> {
        return ObservableTransformer { upstream ->
            upstream.switchMap {
                viewState.value?.takeIf { it.posts.isNotEmpty() }
                    ?.let { Observable.just(it.posts.toLoaded().toSuccess()) }
                    ?: fetchPosts()
            }
        }
    }

    private fun onRetry(): ObservableTransformer<PostsEvents.Retry, State<PostsEventResult>> {
        return ObservableTransformer { upstream ->
            upstream.switchMap { fetchPosts() }
        }
    }

    /* ******************* */
    /*     Side effects    */
    /* ******************* */

    private fun fetchPosts(): Observable<State<PostsEventResult>> {
        return postsRepository.fetchPosts()
            .subscribeOn(RxSchedulers.io())
            .map {
                it.toLoaded().toSuccess()
            }
            .onErrorResumeNext { t: Throwable ->
                Observable.just(t.toFailed().toError())
            }
            .startWith(State.Loading())
    }

    /* ****************** */
    /*       Reducer      */
    /* ****************** */

    private fun resultToState(): ObservableTransformer<State<out PostsEventResult>, out PostsViewState>? {
        return ObservableTransformer { upstream ->
            upstream.scan(viewState.value ?: PostsViewState()) { viewState, state ->
                when (state) {
                    is State.Loading -> viewState.copy(posts = emptyList(),
                                                       isLoading = true,
                                                       error = null)
                    is State.Success -> when (state.packet) {
                        is PostsEventResult.PostsLoaded -> viewState.copy(posts = state.packet.posts,
                                                                          isLoading = false,
                                                                          error = null)
                        else -> viewState
                    }
                    is State.Error -> viewState.copy(posts = emptyList(),
                                                     isLoading = false,
                                                     error = (state.packet as PostsEventResult.LoadingFailed).throwable.message)
                }
            }
                .distinctUntilChanged()
        }
    }


    private fun handleInputsError(throwable: Throwable) {
        Timber.e(throwable, "something went wrong processing input events")
    }
}


interface InputsConsumer<T> {
    fun registerToInputs(vararg inputSources: Observable<out T>): Disposable
}

interface ViewStateProvider<T, U> {
    fun observeViewState(): Observable<T>
    fun observeViewEffects(): Observable<U>
}

class TestViewModel(private val postsRepository: PostsRepository) :
    BaseViewModel<PostsViewModel, PostsViewState, PostsEventResult, PostsViewEffect>() {

    override fun eventToResult(): ObservableTransformer<PostsViewModel, State<out PostsEventResult>> {
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

    /* ******************* */
    /*     Side effects    */
    /* ******************* */

    private fun fetchPosts(): Observable<State<PostsEventResult>> {
        return postsRepository.fetchPosts()
            .subscribeOn(RxSchedulers.io())
            .map {
                it.toLoaded().toSuccess()
            }
            .onErrorResumeNext { t: Throwable ->
                Observable.just(t.toFailed().toError())
            }
            .startWith(State.Loading())
    }

    override fun resultToState(): ObservableTransformer<State<out PostsEventResult>, PostsViewState> {
        return ObservableTransformer { upstream ->
            upstream.scan(viewState.value ?: PostsViewState()) { viewState, state ->
                when (state) {
                    is State.Loading -> viewState.copy(posts = emptyList(),
                                                       isLoading = true,
                                                       error = null)
                    is State.Success -> when (state.packet) {
                        is PostsEventResult.PostsLoaded -> viewState.copy(posts = state.packet.posts,
                                                                          isLoading = false,
                                                                          error = null)
                        else -> viewState
                    }
                    is State.Error -> viewState.copy(posts = emptyList(),
                                                     isLoading = false,
                                                     error = (state.packet as PostsEventResult.LoadingFailed).throwable.message)
                }
            }
                .distinctUntilChanged()
        }
    }

    override fun resultToEffect(): ObservableTransformer<State<out PostsEventResult>, PostsViewEffect> {
        return ObservableTransformer { upstream ->
            upstream.filter { it is State.Success && it.packet is PostsEventResult.NavigateToDetail }
                .map<PostsViewEffect> {

                    val navigationResult =
                        (it as State.Success).packet as PostsEventResult.NavigateToDetail
                    PostsViewEffect.NavigateToPostDetail(navigationResult.post)
                }
        }
    }

    /* ****************** */
    /*      Use cases     */
    /* ****************** */

    private fun onPostClicked(): ObservableTransformer<PostsEvents.PostClicked, State<PostsEventResult.NavigateToDetail>> {
        return ObservableTransformer { upstream ->
            upstream.map<State<PostsEventResult.NavigateToDetail>> {
                PostsEventResult.NavigateToDetail(it.post).toSuccess()
            }
        }
    }

    private fun onScreenLoad(): ObservableTransformer<PostsEvents.ScreenLoad, State<PostsEventResult>> {
        return ObservableTransformer { upstream ->
            upstream.switchMap {
                viewState.value?.takeIf { it.posts.isNotEmpty() }
                    ?.let { Observable.just(it.posts.toLoaded().toSuccess()) }
                    ?: fetchPosts()
            }
        }
    }

    private fun onRetry(): ObservableTransformer<PostsEvents.Retry, State<PostsEventResult>> {
        return ObservableTransformer { upstream ->
            upstream.switchMap { fetchPosts() }
        }
    }

}

abstract class BaseViewModel<T, U, R, N> : ViewModel(), InputsConsumer<T>, ViewStateProvider<U, N> {
    protected var viewModelDisposable: Disposable? = null

    protected val eventsObservable = PublishSubject.create<T>()
    protected val viewState = BehaviorSubject.create<U>()
    protected val viewEffects = PublishSubject.create<N>()

    init {
        val viewChanges = eventsObservable
//            .doOnNext { Timber.d("==== event received ${it.javaClass.simpleName}") }
            .compose(eventToResult())
//            .doOnNext { Timber.d("==== transformed to result ${it.javaClass.simpleName}") }
            .publish()

        viewChanges.compose(resultToState()).subscribe(viewState)
        viewChanges.compose(resultToEffect()).subscribe(viewEffects)

        viewChanges.autoConnect(0) { viewModelDisposable = it }
    }

    /**
     * Transformation encapsulating side effects.
     */
    abstract fun eventToResult(): ObservableTransformer<T, State<out R>>

    /**
     * Transformation from PostsEventResult to ViewState
     */
    abstract fun resultToState(): ObservableTransformer<State<out R>, U>

    /**
     * Transformation from PostsEventResult to ViewEffect
     */
    abstract fun resultToEffect(): ObservableTransformer<State<out R>, N>

    override fun registerToInputs(vararg inputSources: Observable<out T>): Disposable {
        return Observable.mergeArray(*inputSources)
            .subscribe(eventsObservable::onNext, ::handleInputsError)
    }

    override fun observeViewState(): Observable<U> = viewState

    override fun observeViewEffects(): Observable<N> = viewEffects

    private fun handleInputsError(throwable: Throwable) {
        Timber.e(throwable, "something went wrong processing input events")
    }
}

