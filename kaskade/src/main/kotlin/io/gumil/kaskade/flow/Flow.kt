package io.gumil.kaskade.flow

open class MutableFlow<T> : Flow<T> {

    protected open var subscription: ((T) -> Unit)? = null

    open fun sendValue(value: T) {
        subscription?.invoke(value)
    }

    override fun subscribe(subscription: (T) -> Unit) {
        this.subscription = subscription
    }

    override fun unsubscribe() {
        subscription = null
    }
}

interface Flow<T> {

    fun subscribe(subscription: (T) -> Unit)

    fun unsubscribe()
}
