package io.gumil.kaskade.rx

import io.gumil.kaskade.*
import io.reactivex.Observable
import io.reactivex.Observer
import kotlin.reflect.KClass

class RxReducer<ACTION: Action, STATE: State>(
        private val observer: Observer<STATE>,
        private val transformerFunction: ActionState<ACTION, STATE>.() -> Observable<STATE>
) : Reducer<ACTION, STATE> {

    override fun invoke(action: ACTION, state: STATE, onStateChanged: (state: STATE) -> Unit) {
        transformerFunction(ActionState(action, state))
                .doOnNext { onStateChanged(it) }
                .subscribe(observer)
    }
}

@KaskadeBuilderMarker
class RxKaskadeBuilder<ACTION: Action, STATE: State>(
        private val builder: Kaskade.Builder<ACTION, STATE>
) {

    inline fun <reified T : ACTION> on(
            observer: Observer<STATE>,
            noinline transformer: ActionState<T, STATE>.() -> Observable<STATE>
    ) {
        on(T::class, RxReducer(observer, transformer))
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : ACTION> on(clazz: KClass<T>, reducer: Reducer<T, STATE>) {
        builder.on(clazz, reducer)
    }
}

fun <A : Action, S : State> Kaskade.Builder<A, S>.rx(builder: RxKaskadeBuilder<A, S>.() -> Unit) {
    builder(RxKaskadeBuilder(this))
}