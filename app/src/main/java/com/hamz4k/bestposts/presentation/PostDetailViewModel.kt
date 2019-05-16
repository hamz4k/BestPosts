package com.hamz4k.bestposts.presentation

import androidx.lifecycle.ViewModel
import com.hamz4k.bestposts.model.PostDetailEvents
import com.hamz4k.bestposts.model.PostDetailViewState
import com.hamz4k.bestposts.model.toPost
import com.hamz4k.bestposts.model.toViewState
import com.hamz4k.domain.RxSchedulers
import com.hamz4k.domain.posts.PostsRepository
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

class PostDetailViewModelFactory @Inject constructor(private val postsRepository: PostsRepository) {
    fun supply() = PostDetailViewModel(postsRepository = postsRepository)
}

class PostDetailViewModel(private val postsRepository: PostsRepository) : ViewModel(),
    InputsConsumer<PostDetailEvents> {

    private var viewModelDisposable: Disposable? = null

    private val eventsObservable = PublishSubject.create<PostDetailEvents>()
    private val viewState = BehaviorSubject.create<PostDetailViewState>()

    init {
        val viewChanges = eventsObservable
            .compose(eventToState())
            .publish()
        viewChanges.subscribe(viewState)
        viewChanges.autoConnect(0) { viewModelDisposable = it }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelDisposable?.dispose()
    }

    /* **************** */
    /*      Public      */
    /* **************** */
    override fun registerToInputs(vararg inputSources: Observable<out PostDetailEvents>): Disposable {
        return Observable.mergeArray(*inputSources)
            .subscribe(eventsObservable::onNext, ::handleError)
    }

    fun observeViewState(): Observable<out PostDetailViewState> {
        return viewState
    }

    /* ****************** */
    /*       Reducer      */
    /* ****************** */
    private fun eventToState(): ObservableTransformer<PostDetailEvents, PostDetailViewState> {
        return ObservableTransformer { upstream ->
            upstream.publish { eventsObservable ->
                    eventsObservable.ofType(PostDetailEvents.ScreenLoad::class.java).compose(
                        onScreenLoad())

            }
        }
    }

    /* ****************** */
    /*      Use cases     */
    /* ****************** */
    private fun onScreenLoad(): ObservableTransformer<PostDetailEvents.ScreenLoad, PostDetailViewState> {
        return ObservableTransformer { upstream ->
            upstream
                .switchMap {
                    postsRepository.fetchPostDetails(it.post.toPost())
                        .subscribeOn(RxSchedulers.io())
                        .map { postDetail ->
                            postDetail.toViewState()
                        }
                        .onErrorResumeNext { t: Throwable ->
                            Observable.just(PostDetailViewState(error = "Loading failed please retry"))
                        }
                }
                .startWith(PostDetailViewState(isLoading = true))
        }


    }

    private fun handleError(throwable: Throwable) {
        Timber.e(throwable, "something went wrong processing input events")
    }

    sealed class PostDetailItem {
        data class Detail(
            val author: String,
            val avatarUrl: String,
            val title: String,
            val body: String

        ) : PostDetailItem()

        data class CommentHeader(val count: Int) : PostDetailItem()

        data class Comment(
            val id: Int,
            val title: String,
            val email: String,
            val body: String
        ) : PostDetailItem()
    }

}