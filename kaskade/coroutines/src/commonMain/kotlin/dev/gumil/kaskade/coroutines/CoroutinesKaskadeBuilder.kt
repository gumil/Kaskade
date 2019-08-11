package dev.gumil.kaskade.coroutines

import dev.gumil.kaskade.Action
import dev.gumil.kaskade.ActionState
import dev.gumil.kaskade.Kaskade
import dev.gumil.kaskade.KaskadeBuilderMarker
import dev.gumil.kaskade.Reducer
import dev.gumil.kaskade.State
import kotlin.reflect.KClass
import kotlinx.coroutines.CoroutineScope

/**
 * Builder DSL to create reducers with suspending functions with independent scopes.
 *
 * @param builder the default builder.
 */
@KaskadeBuilderMarker
class CoroutinesKaskadeBuilder<ACTION : Action, STATE : State>(
    private val builder: Kaskade.Builder<ACTION, STATE>
) {

    /**
     * Reified version of the on method for better syntax in the DSL.
     *
     * @param scope coroutine scope used by the transformer.
     * @param transformer suspending function that transforms action and a state to new state.
     * @return the [ScopedReducer] associated with the action
     */
    inline fun <reified T : ACTION> on(
        scope: CoroutineScope,
        noinline transformer: suspend ActionState<T, STATE>.() -> STATE
    ): ScopedReducer<T, STATE> {
        val reducer = ScopedReducer(scope, transformer)
        on(T::class, reducer)
        return reducer
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
 * @param builder the default builder from Kaskade.
 */
@KaskadeBuilderMarker
class CoroutinesScopedKaskadeBuilder<ACTION : Action, STATE : State>(
    val scope: CoroutineScope,
    private val builder: Kaskade.Builder<ACTION, STATE>
) {

    /**
     * Reified version of the on method for better syntax in the DSL.
     *
     * @param transformer suspending function that transforms action and a state to new state.
     * @return the [ScopedReducer] associated with the action
     */
    inline fun <reified T : ACTION> on(
        noinline transformer: suspend ActionState<T, STATE>.() -> STATE
    ): ScopedReducer<T, STATE> {
        val reducer = ScopedReducer(scope, transformer)
        on(T::class, reducer)
        return reducer
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
