package com.gumil.kaskade.coroutines

import io.gumil.kaskade.Action
import io.gumil.kaskade.ActionState
import io.gumil.kaskade.Reducer
import io.gumil.kaskade.State
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * [ScopedReducer] launches a suspending function that takes the current state and an action and returns a new state.
 *
 * @param coroutineScope to be used in launching [transformerFunction].
 * @param transformerFunction suspending function specifying transformation to new state.
 */
data class ScopedReducer<ACTION : Action, STATE : State>(
    private val coroutineScope: CoroutineScope,
    private val transformerFunction: suspend ActionState<ACTION, STATE>.() -> STATE
) : Reducer<ACTION, STATE> {

    override fun invoke(action: ACTION, state: STATE, onStateChanged: (state: STATE) -> Unit) {
        startJob(action, state, onStateChanged)
    }

    internal fun startJob(action: ACTION, state: STATE, onStateChanged: (state: STATE) -> Unit) =
        coroutineScope.launch { onStateChanged(transformerFunction(ActionState(action, state))) }
}
