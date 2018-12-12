package io.gumil.kaskade

import kotlin.reflect.KClass

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

class DamFlow<T : Any> : Flow<T>() {

    private val savedValues = mutableMapOf<KClass<out T>, T>()

    private val excludedValues = mutableSetOf<KClass<out T>>()

    override fun subscribe(subscription: (T) -> Unit) {
        super.subscribe(subscription)
        savedValues.forEach {
            super.sendValue(it.value)
        }
    }

    override fun sendValue(value: T) {
        if (value::class !in excludedValues) {
            savedValues[value::class] = value
        }
        super.sendValue(value)
    }

    override fun unsubscribe() {
        super.unsubscribe()
        savedValues.clear()
        excludedValues.clear()
    }

    fun exclude(vararg classes: KClass<out T>) {
        excludedValues.addAll(classes)
    }
}

fun <S : State, A : Action> Kaskade<S, A>.stateFlow(initialAction: A? = null): Flow<S> {
    return createFlow(Flow(), initialAction)
}

fun <S : State, A : Action> Kaskade<S, A>.stateDamFlow(initialAction: A? = null): DamFlow<S> {
    return createFlow(DamFlow(), initialAction)
}

private fun <A : Action, S : State, F: Flow<S>> Kaskade<S, A>.createFlow(flow: F, initialAction: A?): F {
    onStateChanged = { flow.sendValue(it) }
    return flow.also {
        initialAction?.let { action -> process(action) }
    }
}