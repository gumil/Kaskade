package io.gumil.kaskade.rx

import io.gumil.kaskade.Action
import io.gumil.kaskade.Kaskade
import io.gumil.kaskade.State
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.subjects.PublishSubject

/**
 * Extension function for building the rx DSL with independent observers.
 *
 * @param builder function to build the DSL.
 */
fun <A : Action, S : State> Kaskade.Builder<A, S>.rx(builder: RxKaskadeBuilder<A, S>.() -> Unit) {
    builder(RxKaskadeBuilder(this))
}

/**
 * Extension function for building the rx DSL with a shared observer.
 *
 * @param observer to subscribe to observables from [RxReducer].
 * @param builder function to build the DSL.
 */
fun <A : Action, S : State> Kaskade.Builder<A, S>.rx(
    observer: () -> Observer<S>,
    builder: RxSharedObserverKaskadeBuilder<A, S>.() -> Unit
) {
    builder(RxSharedObserverKaskadeBuilder(observer, this))
}

/**
 * @return [Observable] of states from Kaskade.
 */
fun <A : Action, S : State> Kaskade<A, S>.stateObservable(): Observable<S> {
    val observable = PublishSubject.create<S>()
    onStateChanged = {
        observable.onNext(it)
    }
    return observable
}
