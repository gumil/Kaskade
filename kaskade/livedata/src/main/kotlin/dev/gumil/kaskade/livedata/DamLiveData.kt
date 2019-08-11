package dev.gumil.kaskade.livedata

import androidx.lifecycle.MutableLiveData
import dev.gumil.kaskade.flow.SavedValueHolder

/**
 * Dam version of [androidx.lifecycle.LiveData].
 *
 * @see dev.gumil.kaskade.flow.DamEmitter
 */
class DamLiveData<T : Any> : MutableLiveData<T>() {

    private val savedValueHolder = SavedValueHolder<T>()

    override fun onActive() {
        super.onActive()
        for (savedValue in savedValueHolder.savedValues) {
            setValue(savedValue.value)
        }
    }

    override fun setValue(value: T) {
        savedValueHolder.saveValue(value)
        super.setValue(value)
    }

    override fun postValue(value: T) {
        savedValueHolder.saveValue(value)
        super.postValue(value)
    }

    /**
     * Clears all saved values.
     */
    fun clear() {
        savedValueHolder.clear()
    }
}
