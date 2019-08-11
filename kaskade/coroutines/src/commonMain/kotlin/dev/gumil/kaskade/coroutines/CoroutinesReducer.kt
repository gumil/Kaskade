package dev.gumil.kaskade.coroutines

import dev.gumil.kaskade.Action
import dev.gumil.kaskade.ActionState
import dev.gumil.kaskade.Reducer
import dev.gumil.kaskade.State
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/**
 * [CoroutinesReducer] base class that launches a suspending function that takes the current state and an action
 * and returns a new state.
 *
 * @see ScopedReducer
 * @param coroutineScope to be used in launching function.
 */
abstract class CoroutinesReducer<ACTION : Action, STATE : State>(
    private val coroutineScope: CoroutineScope
) : Reducer<ACTION, STATE> {

    private var job: Job? = null

    /**
     * Invokes the function and emits its result depending on the scope
     */
    override fun invoke(action: ACTION, state: STATE, onStateChanged: (state: STATE) -> Unit) {
        job = coroutineScope.reducerJob(action, state, onStateChanged)
    }

    /**
     * @return job that will be executed by the reducer
     */
    abstract fun CoroutineScope.reducerJob(action: ACTION, state: STATE, onStateChanged: (state: STATE) -> Unit): Job

    /**
     * Waits for current job of this reducer to finish.
     */
    suspend fun await() {
        job?.join()
    }
}

/**
 * [ScopedReducer] launches a suspending function that takes the current state and an action and returns a new state.
 *
 * @param coroutineScope to be used in launching function.
 * @param transformerFunction suspending function specifying transformation to new state.
 */
class ScopedReducer<ACTION : Action, STATE : State>(
    coroutineScope: CoroutineScope,
    private val transformerFunction: suspend ActionState<ACTION, STATE>.() -> STATE
) : CoroutinesReducer<ACTION, STATE>(coroutineScope) {

    override fun CoroutineScope.reducerJob(action: ACTION, state: STATE, onStateChanged: (state: STATE) -> Unit): Job {
        return launch { onStateChanged(transformerFunction(ActionState(action, state))) }
    }
}

/**
 * [FlowReducer] launches a coroutines flow asynchronous function that takes the current state and an action and
 * returns a new state.
 *
 * @param coroutineScope to be used in launching function.
 * @param transformerFunction suspending function specifying transformation to new state.
 */
class FlowReducer<ACTION : Action, STATE : State>(
    coroutineScope: CoroutineScope,
    private val transformerFunction: suspend Flow<ActionState<ACTION, STATE>>.() -> Flow<STATE>
) : CoroutinesReducer<ACTION, STATE>(coroutineScope) {

    override fun CoroutineScope.reducerJob(action: ACTION, state: STATE, onStateChanged: (state: STATE) -> Unit): Job {
        return launch {
            transformerFunction(flow { emit(ActionState(action, state)) })
                .collect {
                    onStateChanged(it)
                }
        }
    }
}
