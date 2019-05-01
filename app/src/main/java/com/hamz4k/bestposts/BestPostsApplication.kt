package com.hamz4k.bestposts

import android.app.Activity
import android.app.Application
import io.reactivex.android.schedulers.AndroidSchedulers
import com.hamz4k.bestposts.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import javax.inject.Inject

class BestPostsApplication: Application(), HasActivityInjector {
    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector(): AndroidInjector<Activity> {
        return androidInjector
    }

    override fun onCreate() {
        super.onCreate()
//        GithubSchedulers.init(mainThread = AndroidSchedulers.mainThread())

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        DaggerAppComponent
            .factory()
            .create(this)
            .inject(this)
    }
}