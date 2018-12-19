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

@KaskadeBuilderMarker
class RxKaskadeBuilder<ACTION : Action, STATE : State>(
    private val builder: Kaskade.Builder<ACTION, STATE>
) {

    inline fun <reified T : ACTION> on(
        noinline observer: () -> Observer<STATE>,
        noinline transformer: Observable<ActionState<T, STATE>>.() -> Observable<STATE>
    ) {
        on(T::class, RxReducer(observer, transformer))
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : ACTION> on(clazz: KClass<T>, reducer: Reducer<T, STATE>) {
        builder.on(clazz, reducer)
    }
}

@KaskadeBuilderMarker
class RxSharedObserverKaskadeBuilder<ACTION : Action, STATE : State>(
    val observer: () -> Observer<STATE>,
    private val builder: Kaskade.Builder<ACTION, STATE>
) {

    inline fun <reified T : ACTION> on(
        noinline transformer: Observable<ActionState<T, STATE>>.() -> Observable<STATE>
    ) {
        on(T::class, RxReducer(observer, transformer))
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : ACTION> on(clazz: KClass<T>, reducer: Reducer<T, STATE>) {
        builder.on(clazz, reducer)
    }
}