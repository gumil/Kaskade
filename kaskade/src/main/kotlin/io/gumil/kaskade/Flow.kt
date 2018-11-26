package io.gumil.kaskade

class Flow<T> {

    private var subscription: ((T) -> Unit)? = null

    fun subscribe(subscription: (T) -> Unit) {
        this.subscription = subscription
    }

    fun sendValue(value: T) {
        subscription?.invoke(value)
    }

    fun unsubscribe() {
        subscription = null
    }
}

fun <S : State, A : Action> Kaskade<S, A>.stateFlow(): Flow<S> {
    val state = Flow<S>()
    onStateChanged = { state.sendValue(it) }
    return state
}