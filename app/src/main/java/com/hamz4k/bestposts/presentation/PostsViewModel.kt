package com.hamz4k.bestposts.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hamz4k.domain.posts.PostsRepository
import com.hamz4k.domain.posts.model.Post
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

class PostsViewModelFactory @Inject constructor(val postsRepository: PostsRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PostsViewModel(postsRepository = postsRepository) as T
    }
}

class PostsViewModel(val postsRepository: PostsRepository) : ViewModel(), InputsConsumer<PostsEvents> {

    private var viewModelDisposable: Disposable? = null

    private val eventsObservable = PublishSubject.create<PostsEvents>()
    private val viewState = BehaviorSubject.create<PostsViewState>()

    init {
        val viewChanges = eventsObservable
            .compose(eventToState())
            .publish()
        viewChanges.subscribe(viewState)
        viewChanges.autoConnect(0) { viewModelDisposable = it }
    }


    override fun registerToInputs(vararg inputSources: Observable<out PostsEvents>): Disposable {
        return Observable.mergeArray(*inputSources)
            .subscribe(eventsObservable::onNext, ::handleError)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelDisposable?.dispose()
    }

    /**
     * PUBLIC
     */

    fun observeViewState(): Observable<PostsViewState> {
        return viewState
    }


    private fun handleError(throwable: Throwable) {
        Timber.e(throwable, "something went wrong processing input events")
    }

    private fun eventToState(): ObservableTransformer<PostsEvents, PostsViewState> {
        return ObservableTransformer { upstream ->
            upstream.publish { eventsObservable ->
                Observable.merge(
                    eventsObservable.ofType(PostsEvents.ScreenLoad::class.java).compose(onScreenLoad()),
                    eventsObservable.ofType(PostsEvents.PostClicked::class.java).compose(onPostClicked())
                )
            }
        }
    }

    private fun onPostClicked(): ObservableTransformer<PostsEvents.PostClicked, PostsViewState> {
        return ObservableTransformer { upstream -> upstream.map { viewState.value ?: PostsViewState() } }
    }

    private fun onScreenLoad(): ObservableTransformer<PostsEvents.ScreenLoad, PostsViewState> {
        return ObservableTransformer { upstream ->
            upstream.switchMap {
                postsRepository.fetchPosts()
                    .subscribeOn(Schedulers.io())
                    .map {
                        PostsViewState(posts = it)
                    }
                    .onErrorResumeNext { t: Throwable ->
                        Observable.just(PostsViewState(error = "Loading failed please retry"))
                    }

            }
        }
    }

}


sealed class PostsEvents {
    object ScreenLoad : PostsEvents()
    data class PostClicked(val id: Int) : PostsEvents()
}

fun PostsEvents.ScreenLoad.toState() = PostsViewState(isLoading = true)

data class PostsViewState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

//data class Post(val id: String)

interface InputsConsumer<T> {
    fun registerToInputs(vararg inputSources: Observable<out T>): Disposable
}

