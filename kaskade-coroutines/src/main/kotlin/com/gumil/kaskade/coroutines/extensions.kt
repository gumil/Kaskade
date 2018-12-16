package com.gumil.kaskade.coroutines

import io.gumil.kaskade.Action
import io.gumil.kaskade.Kaskade
import io.gumil.kaskade.Reducer
import io.gumil.kaskade.State
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

fun <A : Action, S : State> Kaskade.Builder<A, S>.coroutines(builder: CoroutinesKaskadeBuilder<A, S>.() -> Unit) {
    builder(CoroutinesKaskadeBuilder(this))
}

fun <A : Action, S : State> Kaskade.Builder<A, S>.coroutines(
        scope: CoroutineScope,
        builder: CoroutinesScopedKaskadeBuilder<A, S>.() -> Unit
) {
    builder(CoroutinesScopedKaskadeBuilder(scope, this))
}

fun <A : Action, S : State> Reducer<A, S>.asJob(action: A, state: S, onStateChanged: (state: S) -> Unit): Job {
    return (this as ScopedReducer).startJob(action, state, onStateChanged)
}