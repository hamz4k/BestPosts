package com.hamz4k.bestposts.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.hamz4k.bestposts.R
import com.hamz4k.bestposts.presentation.PostsEvents
import com.hamz4k.bestposts.presentation.PostsViewModel
import com.hamz4k.bestposts.presentation.PostsViewModelFactory
import com.hamz4k.bestposts.utils.getViewModel
import com.hamz4k.bestposts.utils.rootView
import com.hamz4k.domain.posts.model.Post
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_posts.*
import kotlinx.android.synthetic.main.content_posts.*
import timber.log.Timber
import javax.inject.Inject

class PostsActivity : AppCompatActivity() {

    @Inject
    public lateinit var viewModelFactory: PostsViewModelFactory
    private lateinit var viewModel: PostsViewModel
    private lateinit var listAdapter: PostsAdapter

    private var disposables: CompositeDisposable = CompositeDisposable()
    private val postItemClick: PublishSubject<Post> = PublishSubject.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)
        setSupportActionBar(toolbar)
        viewModel = getViewModel { viewModelFactory.create(PostsViewModel::class.java) }

//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        listAdapter = PostsAdapter { postItemClick.onNext(it) }

        post_list.apply {
            layoutManager = LinearLayoutManager(this@PostsActivity)
            adapter = listAdapter
        }

    }

    override fun onResume() {
        super.onResume()
        val screenLoadEvents: Observable<PostsEvents.ScreenLoad> = Observable.just(PostsEvents.ScreenLoad)
        val navigateToPost: Observable<PostsEvents.PostClicked> = postItemClick
            .map { PostsEvents.PostClicked(it.id) }

        disposables += viewModel.registerToInputs(screenLoadEvents, navigateToPost)

        disposables += viewModel.observeViewState()
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { listAdapter.submitList(it.posts) },
                ::handleError
            )
    }

    private fun handleError(throwable: Throwable) {
        Snackbar.make(rootView, "something went wrong observing view state", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()
        Timber.e(throwable, "something went wrong observing view state")
    }


    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

}