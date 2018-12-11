package io.gumil.kaskade

open class Flow<T> {

    private var subscription: ((T) -> Unit)? = null

    open fun subscribe(subscription: (T) -> Unit) {
        this.subscription = subscription
    }

    open fun sendValue(value: T) {
        subscription?.invoke(value)
    }

    open fun unsubscribe() {
        subscription = null
    }
}

class DamFlow<T> : Flow<T>() {

    private var savedValue: T? = null

    override fun subscribe(subscription: (T) -> Unit) {
        super.subscribe(subscription)
        savedValue?.let { sendValue(it) }
    }

    override fun sendValue(value: T) {
        savedValue = value
        super.sendValue(value)
    }

    override fun unsubscribe() {
        super.unsubscribe()
        savedValue = null
    }
}

fun <S : State, A : Action> Kaskade<S, A>.stateFlow(initialAction: A? = null): Flow<S> {
    return createFlow(Flow(), initialAction)
}

fun <S : State, A : Action> Kaskade<S, A>.stateDamFlow(initialAction: A? = null): Flow<S> {
    return createFlow(DamFlow(), initialAction)
}

private fun <A : Action, S : State> Kaskade<S, A>.createFlow(flow: Flow<S>, initialAction: A?): Flow<S> {
    onStateChanged = { flow.sendValue(it) }
    return flow.also {
        initialAction?.let { process(it) }
    }
}