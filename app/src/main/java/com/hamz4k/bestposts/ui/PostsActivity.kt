package com.hamz4k.bestposts.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ContentLoadingProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hamz4k.bestposts.R
import com.hamz4k.bestposts.model.toUi
import com.hamz4k.bestposts.presentation.*
import com.hamz4k.bestposts.ui.postdetail.PostDetailActivity
import com.hamz4k.bestposts.utils.getViewModel
import com.hamz4k.bestposts.utils.hide
import com.hamz4k.bestposts.utils.makeSnackBar
import com.hamz4k.bestposts.utils.show
import com.hamz4k.domain.RxSchedulers
import com.hamz4k.domain.posts.model.PostLight
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

class PostsActivity : AppCompatActivity() {

    /* **************** */
    /*        DI        */
    /* **************** */
    @Inject
    lateinit var viewModelFactory: PostsViewModelFactory
    private lateinit var viewModel: PostsViewModel

    private var disposables: CompositeDisposable = CompositeDisposable()

    private val postItemClick: PublishSubject<PostLight> = PublishSubject.create()
    private lateinit var listAdapter: PostsAdapter

    private val postsRecyclerView by lazy { findViewById<RecyclerView>(R.id.post_recycler_view) }
    private val postsProgressView by lazy { findViewById<ContentLoadingProgressBar>(R.id.post_loader) }

    /* ***************** */
    /*     Life cycle    */
    /* ***************** */

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)
        setSupportActionBar(findViewById(R.id.toolbar))
        viewModel = getViewModel { viewModelFactory.supply() }

        listAdapter = PostsAdapter { postItemClick.onNext(it) }

        postsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@PostsActivity)
            adapter = listAdapter
        }

    }

    override fun onResume() {
        super.onResume()
        val screenLoadEvents: Observable<PostsEvents.ScreenLoad> = Observable.just(PostsEvents.ScreenLoad)
        val navigateToDetail: Observable<PostsEvents.PostClicked> = postItemClick.map { PostsEvents.PostClicked(it) }

        disposables += viewModel.registerToInputs(screenLoadEvents, navigateToDetail)

        disposables += viewModel.observeViewState()
            .subscribeOn(RxSchedulers.io())
            .observeOn(RxSchedulers.mainThread())
            .subscribe(::updateView, ::handleViewStateError)

        disposables += viewModel.observeViewEffects()
            .subscribeOn(RxSchedulers.io())
            .observeOn(RxSchedulers.mainThread())
            .subscribe(::consumeViewEffect, ::handleViewEffectError)
    }

    override fun onPause() {
        super.onPause()
        disposables.clear()
    }

    /* ********************* */
    /*        Private        */
    /* ********************* */

    private fun consumeViewEffect(viewEffect: PostsViewEffect) {
        when (viewEffect) {
            is PostsViewEffect.NavigateToPostDetail ->
                PostDetailActivity.startActivity(this, viewEffect.post.toUi())
        }
    }

    private fun updateView(viewState: PostsViewState) {
        if (!viewState.isLoading) {
            postsProgressView.hide()
            postsRecyclerView.show()
            listAdapter.submitList(viewState.posts)
        } else {
            postsRecyclerView.hide()
            postsProgressView.show()
        }
    }

    private fun handleViewStateError(throwable: Throwable) {
        postsProgressView.hide()
        postsRecyclerView.hide()
        makeSnackBar("something went wrong observing view state")
        Timber.e(throwable, "something went wrong observing view state")
    }

    private fun handleViewEffectError(throwable: Throwable) {
        makeSnackBar("something went wrong observing view effect")
        Timber.e(throwable, "something went wrong observing view effects")
    }
}