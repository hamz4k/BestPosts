package com.hamz4k.bestposts.ui.posts

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ContentLoadingProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.hamz4k.bestposts.R
import com.hamz4k.bestposts.domain.RxSchedulers
import com.hamz4k.bestposts.domain.posts.PostOverview
import com.hamz4k.bestposts.model.PostsEvents
import com.hamz4k.bestposts.model.PostsViewEffect
import com.hamz4k.bestposts.model.PostsViewState
import com.hamz4k.bestposts.model.toUi
import com.hamz4k.bestposts.presentation.posts.PostsViewModel
import com.hamz4k.bestposts.presentation.posts.PostsViewModelFactory
import com.hamz4k.bestposts.ui.posts.detail.PostDetailActivity
import com.hamz4k.bestposts.utils.*
import com.jakewharton.rxbinding2.view.RxView
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class PostsActivity : AppCompatActivity() {

    /* **************** */
    /*        DI        */
    /* **************** */
    @Inject
    lateinit var viewModelFactory: PostsViewModelFactory
    private lateinit var viewModel: PostsViewModel

    private var disposables: CompositeDisposable = CompositeDisposable()

    private val postItemClickSubject: PublishSubject<PostOverview> = PublishSubject.create()
    private lateinit var listAdapter: PostsAdapter

    private val postsRecyclerView by lazy { findViewById<RecyclerView>(R.id.post_recycler_view) }
    private val postsProgressView by lazy { findViewById<ContentLoadingProgressBar>(R.id.post_loader) }
    private val errorStateView by lazy { findViewById<ViewGroup>(R.id.error_state_layout) }
    private val retryButtonView by lazy { findViewById<Button>(R.id.error_state_retry_button) }

    /* ***************** */
    /*     Life cycle    */
    /* ***************** */

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)
        setSupportActionBar(findViewById(R.id.toolbar))
        viewModel = getViewModel { viewModelFactory.supply() }

        listAdapter = PostsAdapter { postItemClickSubject.onNext(it) }

        postsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@PostsActivity)
            adapter = listAdapter
        }

    }

    override fun onResume() {
        super.onResume()
        val screenLoadEvents: Observable<PostsEvents.ScreenLoad> =
            Observable.just(PostsEvents.ScreenLoad)
        val navigateToDetail: Observable<PostsEvents.PostClicked> =
            postItemClickSubject.map { PostsEvents.PostClicked(it) }
        val retryClicks: Observable<PostsEvents.Retry> =
            RxView.clicks(retryButtonView).map { PostsEvents.Retry }

        disposables += viewModel.registerToInputs(retryClicks,
                                                  screenLoadEvents,
                                                  navigateToDetail)

        disposables += viewModel.observeViewState()
            .subscribeOn(RxSchedulers.io())
            .observeOn(RxSchedulers.mainThread())
            .subscribe(::updateView)

        disposables += viewModel.observeViewEffects()
            .subscribeOn(RxSchedulers.io())
            .observeOn(RxSchedulers.mainThread())
            .subscribe(::consumeViewEffect)
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
            is PostsViewEffect.DisplayErrorSnackbar -> makeSnackBar(viewEffect.message)
        }
    }

    private fun updateView(viewState: PostsViewState) {
        //Loading
        if (viewState.isLoading) {
            postsRecyclerView.hide()
            postsProgressView.show()
            errorStateView.hide()
            return
        }
        //Failure
        viewState.error?.let {
            postsRecyclerView.hide()
            postsProgressView.hide()
            errorStateView.show()

            Snackbar.make(rootView, it, Snackbar.LENGTH_LONG).show()
            return
        }
        // Success
        listAdapter.submitList(viewState.posts)

        postsRecyclerView.show()
        postsProgressView.hide()
        errorStateView.hide()
    }
}