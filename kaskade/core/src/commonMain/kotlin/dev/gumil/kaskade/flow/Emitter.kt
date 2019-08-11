package dev.gumil.kaskade.flow

/**
 * Subclass of [Emitter] that exposes [sendValue] method.
 *
 * Used by [dev.gumil.kaskade.stateEmitter] internally.
 */
open class MutableEmitter<T> : Emitter<T> {

    /**
     * Listen to values emitted by this flow
     */
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
 * [Emitter] applies the observer pattern.
 *
 * It's a simple class to be able to observe states.
 *
 * @see [dev.gumil.kaskade.stateEmitter].
 */
interface Emitter<T> {

    /**
     * @param subscription function that will receive the events.
     */
    fun subscribe(subscription: (T) -> Unit)

    /**
     * Unsubscribes to the [Emitter].
     */
    fun unsubscribe()
}
