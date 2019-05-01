package com.hamz4k.bestposts.di

import android.content.Context
import com.hamz4k.bestposts.BestPostsApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule

@Component(modules = [AndroidInjectionModule::class, AppModule::class, RemoteModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): AppComponent
    }

    fun inject(app: BestPostsApplication)

}