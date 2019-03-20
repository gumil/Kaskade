package dev.gumil.kaskade.flow

import dev.gumil.kaskade.SingleEvent
import kotlin.reflect.KClass

/**
 * [DamFlow] saves values emitted by type. In subscribing states, every type that it is emitted is saved in the dam.
 * It saves every latest emission by type except for [SingleEvent]. SingleEvents are not saved into the Dam.
 */
class DamFlow<T : Any> : MutableFlow<T>() {

    private val savedValueHolder = SavedValueHolder<T>()

    /**
     * Emits all values from [savedValueHolder] when subscribed.
     *
     * @param subscription function that will receive the events.
     */
    override fun subscribe(subscription: (T) -> Unit) {
        super.subscribe(subscription)
        savedValueHolder.savedValues.forEach { super.sendValue(it.value) }
    }

    /**
     * Invokes [subscription] to send events and saves into [savedValueHolder].
     */
    override fun sendValue(value: T) {
        savedValueHolder.saveValue(value)
        super.sendValue(value)
    }

    /**
     * Clears all saved values.
     */
    fun clear() {
        savedValueHolder.clear()
    }
}

/**
 * Saves values by [KClass].
 */
class SavedValueHolder<T : Any> {

    /**
     * [Map] of saved values.
     */
    val savedValues get() = _savedValues

    private val _savedValues = mutableMapOf<KClass<out T>, T>()

    /**
     * Saves values except for [SingleEvent] types.
     *
     * @param value to be saved.
     */
    fun saveValue(value: T) {
        if (value !is SingleEvent) {
            _savedValues[value::class] = value
        }
    }

    /**
     * Clears all saved values.
     */
    fun clear() {
        _savedValues.clear()
    }
}
