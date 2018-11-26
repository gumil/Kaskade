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

import kotlin.properties.Delegates.observable
import kotlin.reflect.KClass

class Kaskade<STATE : State, ACTION : Action> private constructor(
        initialState: STATE
) {

    private val actionResultMap = mutableMapOf<KClass<out ACTION>, (ACTION, STATE) -> STATE>()

    private var currentState: STATE by observable(initialState) { _, _, newValue ->
        onStateChanged?.invoke(newValue)
    }

    var onStateChanged: ((state: STATE) -> Unit)? = null

    fun addActions(builder: Builder<ACTION, STATE>.() -> Unit) {
        val eventBuilder = Builder<ACTION, STATE>()
        eventBuilder.builder()
        actionResultMap.putAll(eventBuilder.transformer)
    }

    fun process(action: ACTION) {
        actionResultMap[action::class]?.invoke(action, currentState)?.let {
            currentState = it
        } ?: throw IncompleteFlowException(action)
    }

    fun unsubscribe() {
        onStateChanged = null
    }

    class Builder<ACTION: Action, STATE: State> internal constructor() {

        private val _transformerMap = mutableMapOf<KClass<ACTION>, (ACTION, STATE) -> STATE>()

        internal val transformer get() = _transformerMap.toMap()

        inline fun <reified T: ACTION> on(noinline transformer: ActionState<T, STATE>.() -> STATE) {
            on(T::class, transformer)
        }

        @Suppress("UNCHECKED_CAST")
        fun <T: ACTION> on(clazz: KClass<T>, transformerFunction: ActionState<T, STATE>.() -> STATE) {
            _transformerMap[clazz as KClass<ACTION>] = { action, state ->
                transformerFunction(ActionState(action as T, state))
            }
        }
    }

    companion object {
        fun <STATE : State, ACTION : Action> create(
                initialState: STATE,
                builder: Builder<ACTION, STATE>.() -> Unit
        ): Kaskade<STATE, ACTION> {
            return Kaskade<STATE, ACTION>(initialState).apply {
                addActions(builder)
            }
        }
    }
}