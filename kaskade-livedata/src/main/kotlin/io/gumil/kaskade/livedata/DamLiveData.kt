package io.gumil.kaskade.livedata

import androidx.lifecycle.MutableLiveData
import io.gumil.kaskade.flow.SavedValueHolder
import kotlin.reflect.KClass

class DamLiveData<T: Any> : MutableLiveData<T>() {

    private val savedValueHolder = SavedValueHolder<T>()

    override fun onActive() {
        super.onActive()
        savedValueHolder.savedValues.forEach { setValue(it.value) }
    }

    override fun setValue(value: T) {
        savedValueHolder.saveValue(value)
        super.setValue(value)
    }

    override fun postValue(value: T) {
        savedValueHolder.saveValue(value)
        super.postValue(value)
    }

    fun exclude(vararg classes: KClass<out T>) {
        savedValueHolder.exclude(*classes)
    }

    fun clear() {
        savedValueHolder.clear()
    }
}