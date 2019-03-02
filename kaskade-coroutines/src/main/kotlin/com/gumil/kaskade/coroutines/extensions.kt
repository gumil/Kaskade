package com.gumil.kaskade.coroutines

import io.gumil.kaskade.Action
import io.gumil.kaskade.Kaskade
import io.gumil.kaskade.State
import kotlinx.coroutines.CoroutineScope

/**
 * Extension function for building the coroutines DSL with independent scopes.
 *
 * @param builder function to build the DSL.
 */
fun <A : Action, S : State> Kaskade.Builder<A, S>.coroutines(builder: CoroutinesKaskadeBuilder<A, S>.() -> Unit) {
    builder(CoroutinesKaskadeBuilder(this))
}

/**
 * Extension function for building the coroutines DSL with a shared scope.
 *
 * @param scope [CoroutineScope] to run suspending functions.
 * @param builder function to build the DSL.
 */
fun <A : Action, S : State> Kaskade.Builder<A, S>.coroutines(
    scope: CoroutineScope,
    builder: CoroutinesScopedKaskadeBuilder<A, S>.() -> Unit
) {
    builder(CoroutinesScopedKaskadeBuilder(scope, this))
}
