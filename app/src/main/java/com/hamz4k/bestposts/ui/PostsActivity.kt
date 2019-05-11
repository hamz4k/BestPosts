package com.hamz4k.bestposts.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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
import kotlinx.android.synthetic.main.activity_posts.*
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

    /* ***************** */
    /*     Life cycle    */
    /* ***************** */

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)
        setSupportActionBar(toolbar)
        viewModel = getViewModel { viewModelFactory.supply() }

        listAdapter = PostsAdapter { postItemClick.onNext(it) }

        post_recycler_view.apply {
            layoutManager = LinearLayoutManager(this@PostsActivity)
            adapter = listAdapter
        }

    }

    override fun onResume() {
        super.onResume()
        val screenLoadEvents: Observable<PostsEvents.ScreenLoad> = Observable.just(PostsEvents.ScreenLoad)
        val navigateToPost: Observable<PostsEvents.PostClicked> = postItemClick
            .map { PostsEvents.PostClicked(it) }

        disposables += viewModel.registerToInputs(screenLoadEvents, navigateToPost)

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
            post_loader.hide()
            post_recycler_view.show()
            listAdapter.submitList(viewState.posts)
        } else {
            post_recycler_view.hide()
            post_loader.show()
        }
    }

    private fun handleViewStateError(throwable: Throwable) {
        post_loader.hide()
        post_recycler_view.hide()
        makeSnackBar("something went wrong observing view state")
        Timber.e(throwable, "something went wrong observing view state")
    }

    private fun handleViewEffectError(throwable: Throwable) {
        makeSnackBar("something went wrong observing view effect")
        Timber.e(throwable, "something went wrong observing view effects")
    }


}