package com.gumil.kaskade.coroutines

import io.gumil.kaskade.Action
import io.gumil.kaskade.ActionState
import io.gumil.kaskade.Kaskade
import io.gumil.kaskade.KaskadeBuilderMarker
import io.gumil.kaskade.Reducer
import io.gumil.kaskade.State
import kotlin.reflect.KClass
import kotlinx.coroutines.CoroutineScope

@KaskadeBuilderMarker
class CoroutinesKaskadeBuilder<ACTION : Action, STATE : State>(
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

@KaskadeBuilderMarker
class CoroutinesScopedKaskadeBuilder<ACTION : Action, STATE : State>(
    val scope: CoroutineScope,
    private val builder: Kaskade.Builder<ACTION, STATE>
) {

    inline fun <reified T : ACTION> on(
        noinline transformer: suspend ActionState<T, STATE>.() -> STATE
    ) {
        on(T::class, ScopedReducer(scope, transformer))
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : ACTION> on(clazz: KClass<T>, reducer: Reducer<T, STATE>) {
        builder.on(clazz, reducer)
    }
}
