package com.hamz4k.bestposts.presentation

import androidx.lifecycle.ViewModel
import com.hamz4k.domain.posts.PostsRepository
import com.hamz4k.domain.posts.model.PostLight
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

class PostsViewModelFactory @Inject constructor(private val postsRepository: PostsRepository) {
    fun supply() = PostsViewModel(postsRepository = postsRepository)
}

class PostsViewModel(val postsRepository: PostsRepository) : ViewModel(), InputsConsumer<PostsEvents> {

    private var viewModelDisposable: Disposable? = null

    private val eventsObservable = PublishSubject.create<PostsEvents>()
    private val viewState = BehaviorSubject.create<PostsViewState>()
    private val viewEffects = PublishSubject.create<PostsViewEffect>()

    init {
        val viewChanges = eventsObservable
            .doOnNext { Timber.d("==== event received ${it.javaClass.simpleName}") }
            .publish()

        viewChanges.compose(eventToState()).subscribe(viewState)
        viewChanges.compose(eventToEffect()).subscribe(viewEffects)

        viewChanges.autoConnect(0) { viewModelDisposable = it }
    }

    private fun eventToEffect(): ObservableTransformer<PostsEvents, PostsViewEffect> {
        return ObservableTransformer { upstream ->
            upstream.filter { it is PostsEvents.PostClicked }
                .map<PostsViewEffect> { PostsViewEffect.NavigateToPostDetail((it as PostsEvents.PostClicked).post) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelDisposable?.dispose()
    }

    /**
     * PUBLIC
     */
    override fun registerToInputs(vararg inputSources: Observable<out PostsEvents>): Disposable {
        return Observable.mergeArray(*inputSources)
            .subscribe(eventsObservable::onNext, ::handleError)
    }

    fun observeViewEffects(): Observable<PostsViewEffect> {
        return viewEffects
    }

    fun observeViewState(): Observable<out PostsViewState> {
        return viewState
    }

    private fun handleError(throwable: Throwable) {
        Timber.e(throwable, "something went wrong processing input events")
    }

    private fun eventToState(): ObservableTransformer<PostsEvents, PostsViewState> {
        return ObservableTransformer { upstream ->
            upstream.publish { eventsObservable ->
                eventsObservable.ofType(PostsEvents.ScreenLoad::class.java).compose(onScreenLoad())
            }
        }
    }

    private fun onScreenLoad(): ObservableTransformer<PostsEvents.ScreenLoad, PostsViewState> {
        return ObservableTransformer { upstream ->
            upstream
                .switchMap {
                    postsRepository.fetchPosts()
                        .subscribeOn(Schedulers.io())
                        .map {
                            PostsViewState(posts = it)
                        }
                        .onErrorResumeNext { t: Throwable ->
                            Observable.just(PostsViewState(error = "Loading failed please retry"))
                        }

                }
                .startWith (PostsViewState(isLoading = true))
        }
    }

}


sealed class PostsEvents {
    object ScreenLoad : PostsEvents()
    data class PostClicked(val post: PostLight) : PostsEvents()
}

data class PostsViewState(
    val posts: List<PostLight> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class PostsViewEffect {
    data class NavigateToPostDetail(val post: PostLight) : PostsViewEffect()
}

interface InputsConsumer<T> {
    fun registerToInputs(vararg inputSources: Observable<out T>): Disposable
}

