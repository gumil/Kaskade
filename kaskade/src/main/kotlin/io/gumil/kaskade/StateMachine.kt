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

package io.gumil.kaskade

import kotlin.properties.Delegates
import kotlin.reflect.KClass

class StateMachine<S : State, A : Action, R : Result<S>>(
        initialState: S
) {
    private val actionResultMap = mutableMapOf<KClass<out A>, Deferred<R>>()

    private var currentState: S by Delegates.observable(initialState) { _, _, newValue ->
        onStateChanged?.invoke(newValue)
    }

    var onStateChanged: ((state: S) -> Unit)? = null

    private val deferredList = mutableListOf<Deferred<*>>()

    fun addAction(clazz: KClass<out A>, deferred: Deferred<R>) {
        deferredList.add(deferred)
        actionResultMap[clazz] = deferred
    }

    fun processAction(action: A) {
        actionResultMap[action::class]?.apply {
            _onNext = {
                currentState = it.reduceToState(currentState)
            }
        }?.invoke() ?: throw IllegalStateException("Action ${action.javaClass.simpleName} not added to State Machine")
    }

    fun processAction(action: Deferred<A>) {
        deferredList.add(action)
        action._onNext = { processAction(it) }
    }

    fun dispose() {
        deferredList.forEach { it.dispose() }
    }
}