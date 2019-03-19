package dev.gumil.kaskade

import dev.gumil.kaskade.flow.DamFlow
import dev.gumil.kaskade.flow.Flow
import dev.gumil.kaskade.flow.MutableFlow

/**
 * @return [Flow] of states from Kaskade.
 */
fun <A : Action, S : State> Kaskade<A, S>.stateFlow(): Flow<S> = createFlow(MutableFlow())

/**
 * @return [DamFlow] of states from Kaskade.
 */
fun <A : Action, S : State> Kaskade<A, S>.stateDamFlow(): DamFlow<S> = createFlow(DamFlow())

private fun <A : Action, S : State, F : MutableFlow<S>> Kaskade<A, S>.createFlow(flow: F): F {
    onStateChanged = { flow.sendValue(it) }
    return flow
}
