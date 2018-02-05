package io.gumil.kaskade.livedata

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import io.gumil.kaskade.Deferred
import io.gumil.kaskade.Intent
import io.gumil.kaskade.Result
import io.gumil.kaskade.State
import io.gumil.kaskade.StateMachine


fun <I, R> ((I) -> LiveData<R>).toDeferred(): (I) -> Deferred<R> {
    return { intent: I ->
        LiveDataDeferredValue(this(intent))
    }
}

fun <S : State, I : Intent, R : Result> StateMachine<S, I, R>.stateLiveData(): LiveData<S> {
    val state = MutableLiveData<S>()
    onStateChanged = {
        state.postValue(it)
    }

    return state
}

class LiveDataDeferredValue<T>(
        private val function: LiveData<T>,
        onNext: (T) -> Unit = {},
        onError: (Throwable) -> Unit = {}
) : Deferred<T>(onNext, onError) {

    private var observer: Observer<T>? = null

    override fun invoke() {

        observer = Observer {
            try {
                it?.let(onNext)
            } catch (e: Exception) {
                onError(e)
            }
        }

        observer?.let {
            function.observeForever(it)
        }
    }

    fun dispose() {
        observer?.let {
            function.removeObserver(it)
        }
    }

}