package com.gumil.kaskade.coroutines

import io.gumil.kaskade.Action
import io.gumil.kaskade.ActionState
import io.gumil.kaskade.Kaskade
import io.gumil.kaskade.KaskadeBuilderMarker
import io.gumil.kaskade.Reducer
import io.gumil.kaskade.State
import kotlin.reflect.KClass
import kotlinx.coroutines.CoroutineScope

/**
 * Builder DSL to create reducers with suspending functions with independent scopes.
 *
 * @param builder the default builder from [Kaskade].
 */
@KaskadeBuilderMarker
class CoroutinesKaskadeBuilder<ACTION : Action, STATE : State>(
    private val builder: Kaskade.Builder<ACTION, STATE>
) {

    /**
     * Reified version of the [on] method for better syntax in the DSL.
     *
     * @param scope to be used by the [transformer].
     * @param transformer suspending function that transforms [ActionState] to new [State].
     */
    inline fun <reified T : ACTION> on(
        scope: CoroutineScope,
        noinline transformer: suspend ActionState<T, STATE>.() -> STATE
    ) {
        on(T::class, ScopedReducer(scope, transformer))
    }

    /**
     * Maps the specified class of type [ACTION] to the specified [reducer].
     *
     * @param clazz key to put in the map
     * @param reducer value mapped to [clazz]
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : ACTION> on(clazz: KClass<T>, reducer: Reducer<T, STATE>) {
        builder.on(clazz, reducer)
    }
}

/**
 * Builder DSL to create reducers with suspending functions with shared scopes.
 *
 * @param scope CoroutineScope to be shared by [ScopedReducer].
 * @param builder the default builder from [Kaskade].
 */
@KaskadeBuilderMarker
class CoroutinesScopedKaskadeBuilder<ACTION : Action, STATE : State>(
    val scope: CoroutineScope,
    private val builder: Kaskade.Builder<ACTION, STATE>
) {

    /**
     * Reified version of the [on] method for better syntax in the DSL.
     *
     * @param transformer suspending function that transforms [ActionState] to new [State].
     */
    inline fun <reified T : ACTION> on(
        noinline transformer: suspend ActionState<T, STATE>.() -> STATE
    ) {
        on(T::class, ScopedReducer(scope, transformer))
    }

    /**
     * Maps the specified class of type [ACTION] to the specified [reducer].
     *
     * @param clazz key to put in the map
     * @param reducer value mapped to [clazz]
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : ACTION> on(clazz: KClass<T>, reducer: Reducer<T, STATE>) {
        builder.on(clazz, reducer)
    }
}
