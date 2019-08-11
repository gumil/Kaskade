package dev.gumil.kaskade

/**
 * [Reducer] is a function that takes the current state and an action and returns a new state.
 */
interface Reducer<ACTION : Action, STATE : State> {

    /**
     * Function called by [Kaskade] to execute the [Reducer].
     *
     * @param action specific action executed by the [Reducer].
     * @param state current state from the [Kaskade] flow.
     * @param onStateChanged should be invoked in the function to execute the state change.
     */
    operator fun invoke(action: ACTION, state: STATE, onStateChanged: (state: STATE) -> Unit)
}

/**
 * Default [Reducer] that runs synchronously.
 *
 * @property transformerFunction function specifying transformation to new state.
 */
class BlockingReducer<ACTION : Action, STATE : State>(
    private val transformerFunction: ActionState<ACTION, STATE>.() -> STATE
) : Reducer<ACTION, STATE> {

    /**
     * Synchronously invokes the function and emits its result.
     */
    override fun invoke(action: ACTION, state: STATE, onStateChanged: (state: STATE) -> Unit) {
        onStateChanged(getState(action, state))
    }

    /**
     * @return [State] invoked from the function
     */
    fun getState(action: ACTION, state: STATE) = transformerFunction(ActionState(action, state))
}
