package com.hamz4k.bestposts.presentation

import io.reactivex.Observable

/**
 * An interface for view state and view effects providers.
 */
interface ViewChangesProvider<T, U> {
    fun observeViewState(): Observable<T>
    fun observeViewEffects(): Observable<U>
}