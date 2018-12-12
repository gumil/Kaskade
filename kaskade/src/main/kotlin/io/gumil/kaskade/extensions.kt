package io.gumil.kaskade

import io.gumil.kaskade.flow.DamFlow
import io.gumil.kaskade.flow.Flow
import io.gumil.kaskade.flow.MutableFlow

fun <S : State, A : Action> Kaskade<S, A>.stateFlow(initialAction: A? = null): Flow<S> {
    return createFlow(MutableFlow(), initialAction)
}

fun <S : State, A : Action> Kaskade<S, A>.stateDamFlow(initialAction: A? = null): DamFlow<S> {
    return createFlow(DamFlow(), initialAction)
}

private fun <A : Action, S : State, F: MutableFlow<S>> Kaskade<S, A>.createFlow(flow: F, initialAction: A?): F {
    onStateChanged = { flow.sendValue(it) }
    return flow.also {
        initialAction?.let { action -> process(action) }
    }
}