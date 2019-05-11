package com.hamz4k.bestposts.ui.postdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hamz4k.bestposts.R
import com.hamz4k.bestposts.presentation.PostDetailEvents
import com.hamz4k.bestposts.presentation.PostDetailViewModel
import com.hamz4k.bestposts.presentation.PostDetailViewModelFactory
import com.hamz4k.bestposts.model.PostUi
import com.hamz4k.bestposts.utils.getViewModel
import com.hamz4k.bestposts.utils.hide
import com.hamz4k.bestposts.utils.makeSnackBar
import com.hamz4k.bestposts.utils.show
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.activity_post_detail.*
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
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = getViewModel { viewModelFactory.supply() }

        postDetailAdapter = DetailAdapter()
        post_detail_recycler_view.apply {
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
            .subscribe {
                if (it.isLoading) {
                    post_detail_loader.show()
                    post_detail_recycler_view.hide()

                } else {
                    post_detail_loader.hide()
                    post_detail_recycler_view.show()
                    postDetailAdapter.submitList(it.detail)
                }
            }
    }

    override fun onPause() {
        super.onPause()
        disposables.clear()
    }

    override fun onSupportNavigateUp(): Boolean {
        return finish().let { true }
    }
}