package io.gumil.kaskade

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.properties.Delegates.observable

internal data class Reducer<ACTION: Action, STATE: State>(
        private val initialState: STATE,
        private val transformerFunction: suspend ActionState<ACTION, STATE>.() -> STATE,
        private val coroutineScope: CoroutineScope? = null
) {

    @Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
    operator fun invoke(action: ACTION, state: STATE, onStateChanged: (state: STATE) -> Unit) {
        var currentState: STATE by observable(initialState) { _, _, newValue ->
            onStateChanged.invoke(newValue)
        }
        coroutineScope?.let {
            it.launch { currentState = transformerFunction(ActionState(action, state)) }
        } ?: runBlocking {
            currentState = transformerFunction(ActionState(action, state))
        }
    }
}
