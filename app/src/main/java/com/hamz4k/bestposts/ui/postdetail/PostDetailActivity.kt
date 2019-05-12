package com.hamz4k.bestposts.ui.postdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.ContentLoadingProgressBar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hamz4k.bestposts.R
import com.hamz4k.bestposts.model.PostUi
import com.hamz4k.bestposts.presentation.PostDetailEvents
import com.hamz4k.bestposts.presentation.PostDetailViewModel
import com.hamz4k.bestposts.presentation.PostDetailViewModelFactory
import com.hamz4k.bestposts.presentation.PostDetailViewState
import com.hamz4k.bestposts.utils.getViewModel
import com.hamz4k.bestposts.utils.hide
import com.hamz4k.bestposts.utils.makeSnackBar
import com.hamz4k.bestposts.utils.show
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

class PostDetailActivity : AppCompatActivity() {

    companion object {

        private const val PARAM_POST = "param_post"

        fun startActivity(source: Context, post: PostUi) {
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
    lateinit var viewModelFactory: PostDetailViewModelFactory
    private lateinit var viewModel: PostDetailViewModel

    private val postDetailProgress by lazy { findViewById<ContentLoadingProgressBar>(R.id.post_detail_loader) }
    private val postDetailRecyclerView by lazy { findViewById<RecyclerView>(R.id.post_detail_recycler_view) }

    private var disposables: CompositeDisposable = CompositeDisposable()
    private lateinit var postDetailAdapter: DetailAdapter

    private lateinit var post: PostUi

    /* ***************** */
    /*     Life cycle    */
    /* ***************** */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)

        intent.extras?.getParcelable<PostUi>(PARAM_POST)?.let {
            post = it
        } ?: run {
            makeSnackBar("Post not found")
            finish()
        }
        setContentView(R.layout.activity_post_detail)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = getViewModel { viewModelFactory.supply() }

        postDetailAdapter = DetailAdapter()
        postDetailRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = postDetailAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        val screenLoadEvents: Observable<PostDetailEvents.ScreenLoad> =
            Observable.just(PostDetailEvents.ScreenLoad(post))

        disposables += viewModel.registerToInputs(screenLoadEvents)

        disposables += viewModel.observeViewState()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::updateView, ::handleViewStateError)
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

    private fun updateView(viewState: PostDetailViewState) {
        if (viewState.isLoading) {
            postDetailProgress.show()
            postDetailRecyclerView.hide()

        } else {
            postDetailProgress.hide()
            postDetailRecyclerView.show()
            postDetailAdapter.submitList(viewState.detail)
        }
    }

    private fun handleViewStateError(throwable: Throwable) {
        postDetailProgress.hide()
        postDetailRecyclerView.hide()
        makeSnackBar("something went wrong observing view state")
        Timber.e(throwable, "something went wrong observing view state")
    }
}