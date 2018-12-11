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

import kotlinx.coroutines.CoroutineScope
import kotlin.properties.Delegates.observable
import kotlin.reflect.KClass

class Kaskade<STATE : State, ACTION : Action> private constructor(
        private val initialState: STATE
) {

    private val actionStateMap = mutableMapOf<KClass<out ACTION>, Reducer<ACTION, STATE>>()

    private var currentState: STATE by observable(initialState) { _, _, newValue ->
        onStateChanged?.invoke(newValue)
    }

    var onStateChanged: ((state: STATE) -> Unit)? = null

    fun addActions(builder: Builder<ACTION, STATE>.() -> Unit) {
        val eventBuilder = Builder<ACTION, STATE>(initialState)
        eventBuilder.builder()
        actionStateMap.putAll(eventBuilder.transformer)
    }

    fun process(action: ACTION) {
        actionStateMap[action::class]?.let { reducer ->
            reducer(action, currentState) { currentState = it }
        } ?: throw IncompleteFlowException(action)

    }

    fun unsubscribe() {
        onStateChanged = null
        actionStateMap.clear()
    }

    class Builder<ACTION : Action, STATE : State> internal constructor(
            private val initialState: STATE
    ) {

        private val _transformerMap = mutableMapOf<KClass<ACTION>, Reducer<ACTION, STATE>>()

        internal val transformer get() = _transformerMap.toMap()

        inline fun <reified T : ACTION> on(noinline transformer: suspend ActionState<T, STATE>.() -> STATE) {
            on(T::class, transformer, null)
        }

        inline fun <reified T : ACTION> on(scope: CoroutineScope, noinline transformer: suspend ActionState<T, STATE>.() -> STATE) {
            on(T::class, transformer, scope)
        }

        @Suppress("UNCHECKED_CAST")
        fun <T : ACTION> on(clazz: KClass<T>, transformerFunction: suspend ActionState<T, STATE>.() -> STATE, scope: CoroutineScope?) {
            _transformerMap[clazz as KClass<ACTION>] = Reducer(initialState, transformerFunction, scope) as Reducer<ACTION, STATE>
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