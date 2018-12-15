package com.gumil.kaskade.coroutines

import io.gumil.kaskade.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class ScopedReducer<ACTION: Action, STATE: State>(
        private val transformerFunction: suspend ActionState<ACTION, STATE>.() -> STATE,
        private val coroutineScope: CoroutineScope
) : Reducer<ACTION, STATE> {

    override fun invoke(action: ACTION, state: STATE, onStateChanged: (state: STATE) -> Unit) {
        coroutineScope.launch { onStateChanged(transformerFunction(ActionState(action, state))) }
    }
}

inline fun <A : Action, S : State, reified T : A> Kaskade.Builder<A, S>.on(
        scope: CoroutineScope,
        noinline transformer: suspend ActionState<T, S>.() -> S
) {
    on(T::class, ScopedReducer(transformer, scope))
}