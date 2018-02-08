package io.gumil.kaskade.rx

import io.gumil.kaskade.Deferred
import io.gumil.kaskade.Action
import io.gumil.kaskade.Result
import io.gumil.kaskade.State
import io.gumil.kaskade.StateMachine
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

fun <T> Observable<T>.toDeferred(): Deferred<T> = RxDeferredValue(this)

class RxDeferredValue<T>(
        private val function: Observable<T>,
        onError: (Throwable) -> Unit = {}
) : Deferred<T>(onError) {

    private var subscription: Disposable? = null

    override fun invoke() {
        subscription = function.subscribe({
            onNext(it)
        }, {
            onError(it)
        })
    }

    override fun dispose() {
        subscription?.dispose()
    }
}

fun <S : State, A : Action, R : Result<S>> StateMachine<S, A, R>.stateObservable(): Observable<S> =
        PublishSubject.create<S>().apply {
            onStateChanged = {
                onNext(it)
            }
        }