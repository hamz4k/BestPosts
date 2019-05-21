package com.hamz4k.bestposts.presentation

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

/**
 * A base [ViewModel] implementing [ViewEventsConsumer] and [ViewChangesProvider]
 * the components to a simple Unidirectional State Flow.
 */
abstract class BaseViewModel<T, U, R, N> : ViewModel(),
                                           ViewEventsConsumer<T>,
                                           ViewChangesProvider<U, N> {
    protected var viewModelDisposable: Disposable? = null

    protected val viewEventsObservable = PublishSubject.create<T>()

    protected val viewState = BehaviorSubject.create<U>()
    protected val viewEffects = PublishSubject.create<N>()

    /* ***************** */
    /*     Life cycle    */
    /* ***************** */

    override fun onCleared() {
        super.onCleared()
        viewModelDisposable?.dispose()
    }

    /* ****************** */
    /*   Transformations  */
    /* ****************** */

    /**
     * Compose all transformations in a chain.
     */
    protected abstract fun setupViewEventsToViewChangesTransformation()

    /**
     * Transformation encapsulating side effects.
     */
    abstract fun eventToResult(): ObservableTransformer<T, State<out R>>

    /**
     * Transformation from ResultPostsEvent to ViewEffect.
     */
    abstract fun resultToEffect(): ObservableTransformer<State<out R>, N>

    /* ****************** */
    /*       Reducer      */
    /* ****************** */
    /**
     * Transformation from Result to ViewState.
     */
    abstract fun resultToState(): ObservableTransformer<State<out R>, U>

    /* **************** */
    /*      Public      */
    /* **************** */

    override fun registerToInputs(vararg inputSources: Observable<out T>): Disposable {
        return Observable.mergeArray(*inputSources)
            .subscribe(viewEventsObservable::onNext, ::handleInputsError)
    }

    override fun observeViewState(): Observable<U> = viewState

    override fun observeViewEffects(): Observable<N> = viewEffects

    /* ***************** */
    /*      Private      */
    /* ***************** */
    private fun handleInputsError(throwable: Throwable) {
        Timber.e(throwable, "something went wrong processing input events")
    }
}