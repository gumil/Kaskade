package io.gumil.kaskade.flow

/**
 * Subclass of [Flow] that exposes [sendValue] method.
 *
 * Used by [io.gumil.kaskade.stateFlow] internally.
 */
open class MutableFlow<T> : Flow<T> {

    protected open var subscription: ((T) -> Unit)? = null

    /**
     * Invokes [subscription] to send events.
     */
    open fun sendValue(value: T) {
        subscription?.invoke(value)
    }

    /**
     * @param subscription function that will receive the events.
     */
    override fun subscribe(subscription: (T) -> Unit) {
        this.subscription = subscription
    }

    /**
     * Sets the [subscription] to null.
     */
    override fun unsubscribe() {
        subscription = null
    }
}

/**
 * [Flow] applies the observer pattern.
 *
 * It's a simple class to be able to observe states.
 *
 * @see [io.gumil.kaskade.stateFlow].
 */
interface Flow<T> {

    /**
     * @param subscription function that will receive the events.
     */
    fun subscribe(subscription: (T) -> Unit)

    /**
     * Unsubscribes to the [Flow].
     */
    fun unsubscribe()
}
