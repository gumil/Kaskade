package io.gumil.kaskade

import io.gumil.kaskade.flow.DamFlow
import io.gumil.kaskade.flow.Flow
import io.gumil.kaskade.flow.MutableFlow
import kotlin.reflect.KClass

fun <A : Action, S : State> Kaskade<A, S>.stateFlow(): Flow<S> {
    return createFlow(MutableFlow())
}

fun <A : Action, S : State> Kaskade<A, S>.stateDamFlow(
    vararg excludedStates: KClass<out S>
): DamFlow<S> {
    val flow: DamFlow<S> = createFlow(DamFlow())
    flow.exclude(*excludedStates)
    return flow
}

private fun <A : Action, S : State, F : MutableFlow<S>> Kaskade<A, S>.createFlow(flow: F): F {
    onStateChanged = { flow.sendValue(it) }
    return flow
}