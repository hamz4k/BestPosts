package com.hamz4k.bestposts.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

/**
 * Get a viewModel that is bound to the Activity's lifecycle (meaning that it will survive config changes).
 * @param f the factory closure that will instantiate the ViewModel when it does not exist already.
 */
internal inline fun <reified T : ViewModel> AppCompatActivity.getViewModel(crossinline f: () -> T): T {
    return ViewModelProviders.of(this, factory(f)).get(T::class.java).also {
        if (it is LifecycleObserver) {
            lifecycle.addObserver(it)
        }
    }
}


@Suppress("UNCHECKED_CAST")
internal inline fun <VM : ViewModel> factory(crossinline f: () -> VM) =
    object : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return f() as T
        }
    }
