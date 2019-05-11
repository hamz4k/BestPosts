package com.hamz4k.bestposts.presentation

import androidx.lifecycle.ViewModel
import com.hamz4k.bestposts.model.PostUi
import com.hamz4k.domain.RxSchedulers
import com.hamz4k.domain.posts.PostsRepository
import com.hamz4k.domain.posts.model.Comment
import com.hamz4k.domain.posts.model.Post
import com.hamz4k.domain.posts.model.PostLight
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

class PostDetailViewModelFactory @Inject constructor(val postsRepository: PostsRepository) {
    fun supply() = PostDetailViewModel(postsRepository = postsRepository)
}

class PostDetailViewModel(val postsRepository: PostsRepository) : ViewModel(), InputsConsumer<PostDetailEvents> {

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

    /**
     * PUBLIC
     */
    override fun registerToInputs(vararg inputSources: Observable<out PostDetailEvents>): Disposable {
        return Observable.mergeArray(*inputSources)
            .subscribe(eventsObservable::onNext, ::handleError)
    }

    fun observeViewState(): Observable<out PostDetailViewState> {
        return viewState
    }


    private fun handleError(throwable: Throwable) {
        Timber.e(throwable, "something went wrong processing input events")
    }

    private fun eventToState(): ObservableTransformer<PostDetailEvents, PostDetailViewState> {
        return ObservableTransformer { upstream ->
            upstream.publish { eventsObservable ->
                Observable.merge(
                    eventsObservable.ofType(PostDetailEvents.ScreenLoad::class.java).compose(onScreenLoad()),
                    eventsObservable.ofType(PostDetailEvents.CommentsClicked::class.java).compose(onPostClicked())
                )
            }
        }
    }

    private fun onPostClicked(): ObservableTransformer<PostDetailEvents.CommentsClicked, PostDetailViewState> {
        return ObservableTransformer { upstream -> upstream.map { viewState.value ?: PostDetailViewState() } }
    }

    private fun onScreenLoad(): ObservableTransformer<PostDetailEvents.ScreenLoad, PostDetailViewState> {
        return ObservableTransformer { upstream ->
            upstream
                .doOnNext { PostDetailViewState(isLoading = true) }
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
        }
    }

}

sealed class PostDetailItem {
    data class Detail(
        val author: String,
        val avatarUrl: String,
        val title: String,
        val body: String

    ) : PostDetailItem()

    data class CommentHeader(val count: Int): PostDetailItem()

    data class Comment(
        val id: Int,
        val title: String,
        val email: String,
        val body: String
    ) : PostDetailItem()
}

fun PostUi.toPost() = PostLight(
    userId = userId,
    id = id,
    avatarUrl = avatarUrl,
    title = title,
    body = body
)

fun Post.toViewState() = PostDetailViewState(
    detail = mutableListOf(
        PostDetailItem.Detail(
            author = user.name,
            avatarUrl = user.avatarUrl,
            title = title,
            body = body
        ),
        PostDetailItem.CommentHeader(comments.size)
    ).plus(comments.map { it.toCommentItem() }).toList()
)

fun Comment.toCommentItem() = PostDetailItem.Comment(
    id = id,
    title = title,
    email = email,
    body = body
)


sealed class PostDetailEvents {
    data class ScreenLoad(val post: PostUi) : PostDetailEvents()
    data class CommentsClicked(val id: Int) : PostDetailEvents()
}

//fun PostDetailEvents.ScreenLoad.toState() = PostDetailViewState(isLoading = true)

data class PostDetailViewState(
    val detail: List<PostDetailItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
