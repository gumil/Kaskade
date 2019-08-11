package dev.gumil.kaskade.coroutines

import dev.gumil.kaskade.Action
import dev.gumil.kaskade.ActionState
import dev.gumil.kaskade.Reducer
import dev.gumil.kaskade.State
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * [ScopedReducer] launches a suspending function that takes the current state and an action and returns a new state.
 *
 * @param coroutineScope to be used in launching function.
 * @param transformerFunction suspending function specifying transformation to new state.
 */
class ScopedReducer<ACTION : Action, STATE : State>(
    private val coroutineScope: CoroutineScope,
    private val transformerFunction: suspend ActionState<ACTION, STATE>.() -> STATE
) : Reducer<ACTION, STATE> {

    private var job: Job? = null

    /**
     * Invokes the function and emits its result depending on the scope
     */
    override fun invoke(action: ACTION, state: STATE, onStateChanged: (state: STATE) -> Unit) {
        job = coroutineScope.launch { onStateChanged(transformerFunction(ActionState(action, state))) }
    }

    /**
     * Waits for current job of this reducer to finish.
     */
    suspend fun await() {
        job?.join()
    }
}
