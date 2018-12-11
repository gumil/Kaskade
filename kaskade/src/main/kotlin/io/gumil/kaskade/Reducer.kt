package io.gumil.kaskade

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.properties.Delegates

internal class Reducer<ACTION: Action, STATE: State>(
        initialState: STATE,
        private val transformerFunction: suspend ActionState<ACTION, STATE>.() -> STATE,
        private val coroutineScope: CoroutineScope? = null
) {

    private var onStateChanged: ((state: STATE) -> Unit)? = null

    private var currentState: STATE by Delegates.observable(initialState) { _, _, newValue ->
        onStateChanged?.invoke(newValue)
        onStateChanged = null
    }

    operator fun invoke(action: ACTION, state: STATE, onStateChanged: (state: STATE) -> Unit) {
        this.onStateChanged = onStateChanged
        coroutineScope?.let {
            it.launch { currentState = transformerFunction(ActionState(action, state)) }
        } ?: runBlocking {
            currentState = transformerFunction(ActionState(action, state))
        }
    }
}
