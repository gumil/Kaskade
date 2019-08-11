package dev.gumil.kaskade

import dev.gumil.kaskade.flow.DamEmitter
import dev.gumil.kaskade.flow.Emitter
import dev.gumil.kaskade.flow.MutableEmitter

/**
 * @return [Emitter] of states from Kaskade.
 */
fun <A : Action, S : State> Kaskade<A, S>.stateEmitter(): Emitter<S> = createEmitter(MutableEmitter())

/**
 * @return [DamEmitter] of states from Kaskade.
 */
fun <A : Action, S : State> Kaskade<A, S>.stateDamEmitter(): DamEmitter<S> = createEmitter(DamEmitter())

private fun <A : Action, S : State, F : MutableEmitter<S>> Kaskade<A, S>.createEmitter(flow: F): F {
    onStateChanged = { flow.sendValue(it) }
    return flow
}
