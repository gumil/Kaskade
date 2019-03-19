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

package dev.gumil.kaskade.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.gumil.kaskade.Action
import dev.gumil.kaskade.Kaskade
import dev.gumil.kaskade.State

/**
 * @return [LiveData] of states from Kaskade.
 */
fun <A : Action, S : State> Kaskade<A, S>.stateLiveData(): LiveData<S> =
    createLiveData(MutableLiveData())

/**
 * @return [DamLiveData] of states from Kaskade.
 */
fun <A : Action, S : State> Kaskade<A, S>.stateDamLiveData(): DamLiveData<S> =
    createLiveData(DamLiveData())

private fun <A : Action, S : State, L : MutableLiveData<S>> Kaskade<A, S>.createLiveData(
    state: L
): L {
    onStateChanged = {
        state.postValue(it)
    }
    return state
}
