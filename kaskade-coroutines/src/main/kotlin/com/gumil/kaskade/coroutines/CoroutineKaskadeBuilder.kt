package com.gumil.kaskade.coroutines

import io.gumil.kaskade.*
import kotlinx.coroutines.CoroutineScope
import kotlin.reflect.KClass

class CoroutineKaskadeBuilder<ACTION: Action, STATE: State>(
        private val builder: Kaskade.Builder<ACTION, STATE>
) {

    inline fun <reified T : ACTION> on(
            scope: CoroutineScope,
            noinline transformer: suspend ActionState<T, STATE>.() -> STATE
    ) {
        on(T::class, ScopedReducer(scope, transformer))
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : ACTION> on(clazz: KClass<T>, reducer: Reducer<T, STATE>) {
        builder.on(clazz, reducer)
    }
}

fun <A : Action, S : State> Kaskade.Builder<A, S>.coroutines(builder: CoroutineKaskadeBuilder<A, S>.() -> Unit) {
    builder(CoroutineKaskadeBuilder(this))
}