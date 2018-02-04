package io.gumil.kaskade.rx

import io.gumil.kaskade.Deferred
import io.gumil.kaskade.MviIntent
import io.gumil.kaskade.MviResult
import io.gumil.kaskade.MviState
import io.gumil.kaskade.MviStateMachine
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

internal class RxStateMachine<S : MviState, in I : MviIntent, R : MviResult>(
        initialState: S,
        resultFromIntent: (I) -> Observable<R>,
        reducer: (state: S, result: R) -> S
) : MviStateMachine<S, I, R>(
        initialState, resultFromIntent.toDeferred(), reducer
) {
    private val disposables = CompositeDisposable()

    fun getStateObservable(): Observable<S> = stateObservable()

    fun processIntents(intent: Observable<out I>) {
        intent.subscribe { processIntent(it) }.disposeOnCleared()
    }

    fun clearDisposables() {
        disposables.clear()
    }

    private fun Disposable.disposeOnCleared() {
        disposables.add(this)
    }
}

fun <I, R> ((I) -> Observable<R>).toDeferred(): (I) -> Deferred<R> {
    return { intent: I ->
        RxDeferredValue(this(intent))
    }
}

class RxDeferredValue<T>(
        private val function: Observable<T>,
        onNext: (T) -> Unit = {},
        onError: (Throwable) -> Unit = {}
) : Deferred<T>(onNext, onError) {

    private var subscription: Disposable? = null

    override fun invoke() {
        subscription = function.subscribe({
            onNext(it)
        }, {
            onError(it)
        })
    }

    fun dispose() {
        subscription?.dispose()
    }
}

fun <S : MviState, I : MviIntent, R : MviResult> MviStateMachine<S, I, R>.stateObservable():
        Observable<S> = PublishSubject.create<S>().apply {
    onStateChanged = {
        onNext(it)
    }
}