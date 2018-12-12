package io.gumil.kaskade.flow

open class MutableFlow<T>: Flow<T> {

    override var subscription: ((T) -> Unit)? = null

    open fun sendValue(value: T) {
        subscription?.invoke(value)
    }
}

interface Flow<T> {

    var subscription: ((T) -> Unit)?

    fun subscribe(subscription: (T) -> Unit) {
        this.subscription = subscription
    }

    fun unsubscribe() {
        subscription = null
    }
}