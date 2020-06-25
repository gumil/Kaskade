package dev.gumil.kaskade.coroutines

import dev.gumil.kaskade.flow.SavedValueHolder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Dam version of [kotlinx.coroutines.flow.StateFlow].
 *
 * Emits only the last value in savedValueHolder. This is due to the limitation
 * of the StateFlow api.
 *
 * @see dev.gumil.kaskade.flow.DamEmitter
 */
@ExperimentalCoroutinesApi
class DamStateFlow<T : Any>(
    initialState: T
) : MutableStateFlow<T> {

    private val stateFlow = MutableStateFlow(initialState)
    private val savedValueHolder = SavedValueHolder<T>()

    @InternalCoroutinesApi
    override suspend fun collect(collector: FlowCollector<T>) {
        savedValueHolder.savedValues.values.lastOrNull()?.let {
            stateFlow.value = it
        }
        stateFlow.collect(collector)
    }

    override var value: T
        get() = stateFlow.value
        set(value) {
            savedValueHolder.saveValue(value)
            stateFlow.value = value
        }

    /**
     * Clears all saved values.
     */
    fun clear() {
        savedValueHolder.clear()
    }
}
