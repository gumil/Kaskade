package io.gumil.kaskade.rx

import io.gumil.kaskade.Action
import io.gumil.kaskade.Kaskade
import io.gumil.kaskade.State
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.subjects.PublishSubject

fun <A : Action, S : State> Kaskade.Builder<A, S>.rx(builder: RxKaskadeBuilder<A, S>.() -> Unit) {
    builder(RxKaskadeBuilder(this))
}

fun <A : Action, S : State> Kaskade.Builder<A, S>.rx(
        observer: () -> Observer<S>,
        builder: RxSharedObserverKaskadeBuilder<A, S>.() -> Unit
) {
    builder(RxSharedObserverKaskadeBuilder(observer, this))
}

fun <A : Action, S : State> Kaskade<A, S>.stateObservable(): Observable<S> =
        PublishSubject.create<S>().apply {
            onStateChanged = {
                onNext(it)
            }
        }