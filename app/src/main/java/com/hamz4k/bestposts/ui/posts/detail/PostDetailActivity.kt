package com.hamz4k.bestposts.ui.posts.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ContentLoadingProgressBar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hamz4k.bestposts.R
import com.hamz4k.bestposts.domain.RxSchedulers
import com.hamz4k.bestposts.model.UiPostOverview
import com.hamz4k.bestposts.presentation.posts.detail.*
import com.hamz4k.bestposts.utils.getViewModel
import com.hamz4k.bestposts.utils.hide
import com.hamz4k.bestposts.utils.makeSnackBar
import com.hamz4k.bestposts.utils.show
import com.jakewharton.rxbinding2.view.RxView
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import javax.inject.Inject

class PostDetailActivity : AppCompatActivity() {

    companion object {

        private const val PARAM_POST = "param_post"

        fun startActivity(source: Context, post: UiPostOverview) {
            val intent = Intent(source, PostDetailActivity::class.java).apply {
                putExtra(PARAM_POST, post)
            }
            source.startActivity(intent)
        }
    }

    /* **************** */
    /*        DI        */
    /* **************** */
    @Inject
    lateinit var mViewModelFactory: DetailViewModelFactory
    private lateinit var viewModel: DetailViewModel

    private val postDetailProgress by lazy { findViewById<ContentLoadingProgressBar>(R.id.post_detail_loader) }
    private val postDetailRecyclerView by lazy { findViewById<RecyclerView>(R.id.post_detail_recycler_view) }
    private val errorStateView by lazy { findViewById<ViewGroup>(R.id.error_state_layout) }
    private val retryButtonView by lazy { findViewById<Button>(R.id.error_state_retry_button) }

    private var disposables: CompositeDisposable = CompositeDisposable()
    private lateinit var postDetailAdapter: DetailAdapter

    private lateinit var post: UiPostOverview

    /* ***************** */
    /*     Life cycle    */
    /* ***************** */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)

        intent.extras?.getParcelable<UiPostOverview>(PARAM_POST)?.let {
            post = it
        } ?: run {
            makeSnackBar(R.string.error_message)
            finish()
        }
        setContentView(R.layout.activity_post_detail)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = getViewModel { mViewModelFactory.supply() }

        postDetailAdapter = DetailAdapter()
        postDetailRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = postDetailAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        val screenLoadEvents: Observable<DetailEvents.ScreenLoad> =
            Observable.just(DetailEvents.ScreenLoad(post))
        val retryClicks: Observable<DetailEvents.Retry> =
            RxView.clicks(retryButtonView).map { DetailEvents.Retry(post) }

        disposables += viewModel.registerToInputs(retryClicks, screenLoadEvents)

        disposables += viewModel.observeViewState()
            .observeOn(RxSchedulers.mainThread())
            .subscribe(::updateView)

        disposables += viewModel.observeViewEffects()
            .subscribeOn(RxSchedulers.io())
            .subscribe(::consumeViewEffect)
    }

    override fun onPause() {
        super.onPause()
        disposables.clear()
    }

    override fun onSupportNavigateUp(): Boolean {
        return finish().let { true }
    }

    /* ***************** */
    /*      Private      */
    /* ***************** */

    private fun consumeViewEffect(viewEffect: DetailViewEffect) {
        when (viewEffect) {
            is DetailViewEffect.DisplayErrorSnackbar -> makeSnackBar(viewEffect.message)
        }
    }

    private fun updateView(viewState: DetailViewState) {
        //Loading
        if (viewState.isLoading) {
            postDetailRecyclerView.hide()
            postDetailProgress.show()
            errorStateView.hide()
            return
        }
        //Failure
        viewState.error?.let {
            postDetailRecyclerView.hide()
            postDetailProgress.hide()
            errorStateView.show()

            makeSnackBar(it)
            return
        }
        // Success
        postDetailAdapter.submitList(viewState.detail)

        postDetailRecyclerView.show()
        postDetailProgress.hide()
        errorStateView.hide()
    }
}