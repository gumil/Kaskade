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

class Kaskade<ACTION : Action, STATE : State> private constructor(
    initialState: STATE
) {

    private val actionStateMap = mutableMapOf<KClass<out ACTION>, Reducer<ACTION, STATE>>()

    /**
     * Queue to save states that are not emitted while [onStateChanged] is null
     */
    private val stateQueue: MutableList<STATE> = mutableListOf(initialState)

    /**
     * Invokes [onStateChanged] or enqueues state when mutating the value
     * @return current state
     */
    private var currentState: STATE by observable(initialState) { _, _, newValue ->
        onStateChanged?.invoke(newValue) ?: stateQueue.add(newValue)
    }

    /**
     * Listens to state changes on a Kaskade flow
     *
     * When mutating the function, it emits all states that are pending in the [stateQueue]
     */
    var onStateChanged: ((state: STATE) -> Unit)? = null
        set(value) {
            field = value

            stateQueue.forEach {
                currentState = it
            }
            stateQueue.clear()
        }

    fun addActions(builder: Builder<ACTION, STATE>.() -> Unit) {
        val eventBuilder = Builder<ACTION, STATE>()
        eventBuilder.builder()
        actionStateMap.putAll(eventBuilder.transformer)
    }

    fun process(action: ACTION) {
        getReducer(action)?.let { reducer ->
            reducer(action, currentState) { currentState = it }
        } ?: throw IncompleteFlowException(action)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : ACTION> getReducer(action: T): Reducer<T, STATE>? {
        return actionStateMap[action::class] as? Reducer<T, STATE>
    }

    fun unsubscribe() {
        onStateChanged = null
        actionStateMap.clear()
    }

    @KaskadeBuilderMarker
    class Builder<ACTION : Action, STATE : State> internal constructor() {

        private val _transformerMap = mutableMapOf<KClass<out ACTION>, Reducer<ACTION, STATE>>()

        internal val transformer get() = _transformerMap.toMap()

        inline fun <reified T : ACTION> on(noinline transformer: ActionState<T, STATE>.() -> STATE) {
            on(T::class, BlockingReducer(transformer))
        }

        @Suppress("UNCHECKED_CAST")
        fun <T : ACTION> on(clazz: KClass<T>, reducer: Reducer<T, STATE>) {
            _transformerMap[clazz] = reducer as Reducer<ACTION, STATE>
        }
    }

    companion object {
        fun <ACTION : Action, STATE : State> create(
            initialState: STATE,
            builder: Builder<ACTION, STATE>.() -> Unit
        ): Kaskade<ACTION, STATE> {
            return Kaskade<ACTION, STATE>(initialState).apply {
                addActions(builder)
            }
        }
    }
}