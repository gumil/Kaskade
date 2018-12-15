package io.gumil.kaskade

interface Reducer<ACTION: Action, STATE: State> {
    operator fun invoke(action: ACTION, state: STATE, onStateChanged: (state: STATE) -> Unit)
}

data class BlockingReducer<ACTION: Action, STATE: State>(
        private val transformerFunction: ActionState<ACTION, STATE>.() -> STATE
) : Reducer<ACTION, STATE> {
    override fun invoke(action: ACTION, state: STATE, onStateChanged: (state: STATE) -> Unit) {
        onStateChanged(transformerFunction(ActionState(action, state)))
    }
}