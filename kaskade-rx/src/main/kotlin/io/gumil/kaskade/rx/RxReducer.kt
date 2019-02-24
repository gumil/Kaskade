package io.gumil.kaskade.rx

import io.gumil.kaskade.Action
import io.gumil.kaskade.ActionState
import io.gumil.kaskade.Reducer
import io.gumil.kaskade.State
import io.reactivex.Observable
import io.reactivex.Observer

/**
 * [RxReducer] subscribes to observables that takes the current state and an action and returns a new state.
 *
 * @param observer to be used to subscribe to [Observable] from the [transformerFunction].
 * @param transformerFunction function specifying the transformation of [Observable] to new state.
 */
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
