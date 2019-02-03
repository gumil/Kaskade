package io.gumil.kaskade.flow

import io.gumil.kaskade.SingleEvent
import kotlin.reflect.KClass

class DamFlow<T : Any> : MutableFlow<T>() {

    private val savedValueHolder = SavedValueHolder<T>()

    override fun subscribe(subscription: (T) -> Unit) {
        super.subscribe(subscription)
        savedValueHolder.savedValues.forEach { super.sendValue(it.value) }
    }

    override fun sendValue(value: T) {
        savedValueHolder.saveValue(value)
        super.sendValue(value)
    }

    fun clear() {
        savedValueHolder.clear()
    }
}

class SavedValueHolder<T : Any> {
    val savedValues get() = _savedValues

    private val _savedValues = mutableMapOf<KClass<out T>, T>()

    fun saveValue(value: T) {
        if (value !is SingleEvent) {
            _savedValues[value::class] = value
        }
    }

    fun clear() {
        _savedValues.clear()
    }
}