package com.hamz4k.bestposts.presentation

import io.reactivex.Observable
import io.reactivex.disposables.Disposable

/**
 * An interface for view events consumers.
 */
interface ViewEventsConsumer<T> {
    fun registerToInputs(vararg inputSources: Observable<out T>): Disposable
}