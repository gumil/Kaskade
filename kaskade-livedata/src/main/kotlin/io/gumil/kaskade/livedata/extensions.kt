package io.gumil.kaskade.livedata

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import io.gumil.kaskade.Deferred
import io.gumil.kaskade.Action
import io.gumil.kaskade.Result
import io.gumil.kaskade.State
import io.gumil.kaskade.StateMachine

fun <T> LiveData<T>.toDeferred(): Deferred<T> = LiveDataDeferredValue(this)

fun <S : State, A : Action, R : Result<S>> StateMachine<S, A, R>.stateLiveData(): LiveData<S> {
    val state = MutableLiveData<S>()
    onStateChanged = {
        state.postValue(it)
    }

    return state
}

class LiveDataDeferredValue<T>(
        private val function: LiveData<T>,
        onError: (Throwable) -> Unit = {}
) : Deferred<T>(onError) {

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

    override fun dispose() {
        observer?.let {
            function.removeObserver(it)
        }
    }

}