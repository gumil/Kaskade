package io.gumil.kaskade

import io.gumil.kaskade.flow.DamFlow
import io.gumil.kaskade.flow.Flow
import io.gumil.kaskade.flow.MutableFlow

fun <A : Action, S : State> Kaskade<A, S>.stateFlow(): Flow<S> = createFlow(MutableFlow())

fun <A : Action, S : State> Kaskade<A, S>.stateDamFlow(): DamFlow<S> = createFlow(DamFlow())

private fun <A : Action, S : State, F : MutableFlow<S>> Kaskade<A, S>.createFlow(flow: F): F {
    onStateChanged = { flow.sendValue(it) }
    return flow
}
