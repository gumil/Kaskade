/*
 * Copyright 2018 Miguel Panelo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.gumil.kaskade.livedata

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import io.gumil.kaskade.Event
import io.gumil.kaskade.Action
import io.gumil.kaskade.Effect
import io.gumil.kaskade.State
import io.gumil.kaskade.Kaskade

fun <T> LiveData<T>.toDeferred(
        onError: (Throwable) -> Unit = {}
): Event<T> = LiveDataEventValue(this, onError)

fun <S : State, A : Action, R : Effect> Kaskade<S, A, R>.stateLiveData(): LiveData<S> {
    val state = MutableLiveData<S>()
    onStateChanged = {
        state.postValue(it)
    }

    return state
}

class LiveDataEventValue<T>(
        private val function: LiveData<T>,
        onError: (Throwable) -> Unit = {}
) : Event<T>(onError) {

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