package dev.gumil.kaskade.coroutines

import dev.gumil.kaskade.Action
import dev.gumil.kaskade.ActionState
import dev.gumil.kaskade.Reducer
import dev.gumil.kaskade.State
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * [ScopedReducer] launches a suspending function that takes the current state and an action and returns a new state.
 *
 * @param coroutineScope to be used in launching [transformerFunction].
 * @param transformerFunction suspending function specifying transformation to new state.
 */
class ScopedReducer<ACTION : Action, STATE : State>(
    private val coroutineScope: CoroutineScope,
    private val transformerFunction: suspend ActionState<ACTION, STATE>.() -> STATE
) : Reducer<ACTION, STATE> {

    override fun invoke(action: ACTION, state: STATE, onStateChanged: (state: STATE) -> Unit) {
        coroutineScope.launch { onStateChanged(transformerFunction(ActionState(action, state))) }
    }
}
