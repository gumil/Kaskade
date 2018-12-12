package io.gumil.kaskade.flow

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

    override fun unsubscribe() {
        super.unsubscribe()
        savedValueHolder.clear()
    }

    fun exclude(vararg classes: KClass<out T>) {
        savedValueHolder.exclude(*classes)
    }
}

class SavedValueHolder<T : Any> {
    val savedValues get() = _savedValues

    private val _savedValues = mutableMapOf<KClass<out T>, T>()

    private val excludedValues = mutableSetOf<KClass<out T>>()

    fun saveValue(value: T) {
        if (value::class !in excludedValues) {
            _savedValues[value::class] = value
        }
    }

    fun exclude(vararg classes: KClass<out T>) {
        excludedValues.addAll(classes)
    }

    fun clear() {
        _savedValues.clear()
        excludedValues.clear()
    }
}