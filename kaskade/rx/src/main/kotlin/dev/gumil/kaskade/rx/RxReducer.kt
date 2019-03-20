package dev.gumil.kaskade.rx

import dev.gumil.kaskade.Action
import dev.gumil.kaskade.ActionState
import dev.gumil.kaskade.Reducer
import dev.gumil.kaskade.State
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
