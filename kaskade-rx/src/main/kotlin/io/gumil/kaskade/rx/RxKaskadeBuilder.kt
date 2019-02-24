package io.gumil.kaskade.rx

import io.gumil.kaskade.Action
import io.gumil.kaskade.ActionState
import io.gumil.kaskade.Kaskade
import io.gumil.kaskade.KaskadeBuilderMarker
import io.gumil.kaskade.Reducer
import io.gumil.kaskade.State
import io.reactivex.Observable
import io.reactivex.Observer
import kotlin.reflect.KClass

/**
 * Builder DSL to create reducers with independent observers.
 *
 * @param builder the default builder from [Kaskade].
 */
@KaskadeBuilderMarker
class RxKaskadeBuilder<ACTION : Action, STATE : State>(
    private val builder: Kaskade.Builder<ACTION, STATE>
) {

    /**
     * Reified version of the [on] method for better syntax in the DSL.
     *
     * @param observer to be used to subscribe to the [transformer].
     * @param transformer function that transforms [Observable] of [ActionState] to an [Observable] of [State].
     */
    inline fun <reified T : ACTION> on(
        noinline observer: () -> Observer<STATE>,
        noinline transformer: Observable<ActionState<T, STATE>>.() -> Observable<STATE>
    ) {
        on(T::class, RxReducer(observer, transformer))
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
 * Builder DSL to create reducers with a shared observer.
 *
 * @param observer to subscribe to observables from [RxReducer].
 * @param builder the default builder from [Kaskade].
 */
@KaskadeBuilderMarker
class RxSharedObserverKaskadeBuilder<ACTION : Action, STATE : State>(
    val observer: () -> Observer<STATE>,
    private val builder: Kaskade.Builder<ACTION, STATE>
) {

    /**
     * Reified version of the [on] method for better syntax in the DSL.
     *
     * @param transformer function that transforms [Observable] of [ActionState] to an [Observable] of [State].
     */
    inline fun <reified T : ACTION> on(
        noinline transformer: Observable<ActionState<T, STATE>>.() -> Observable<STATE>
    ) {
        on(T::class, RxReducer(observer, transformer))
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
