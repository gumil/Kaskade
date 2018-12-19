package io.gumil.kaskade.rx

import io.gumil.kaskade.Action
import io.gumil.kaskade.ActionState
import io.gumil.kaskade.Reducer
import io.gumil.kaskade.State
import io.reactivex.Observable
import io.reactivex.Observer

class RxReducer<ACTION : Action, STATE : State>(
    private val observer: () -> Observer<STATE>,
    private val transformerFunction: Observable<ActionState<ACTION, STATE>>.() -> Observable<STATE>
) : Reducer<ACTION, STATE> {

    override fun invoke(action: ACTION, state: STATE, onStateChanged: (state: STATE) -> Unit) {
        transformerFunction(Observable.just(ActionState(action, state)))
            .doOnNext { onStateChanged(it) }
            .subscribe(observer())
    }
}