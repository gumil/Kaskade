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

/**
 * [Kaskade] is a predictable state container with simplicity in mind. It does not rely on external dependency to
 * enforce the unidirectional flow. It has three main components which are the [Action], [State], and [Reducer].
 * It takes the actions and pass them to the reducer with the current state to output a new state.
 *
 * @param initialState initial state to be emitted immediately when [onStateChanged] is initialized.
 */
class Kaskade<ACTION : Action, STATE : State> private constructor(
    initialState: STATE
) {

    private val actionStateMap = mutableMapOf<KClass<out ACTION>, Reducer<ACTION, STATE>>()

    private val stateQueue: MutableList<STATE> = mutableListOf(initialState)

    private var currentState: STATE by observable(initialState) { _, _, newValue ->
        emitOrEnqueueState(newValue)
    }

    private fun emitOrEnqueueState(newValue: STATE) {
        onStateChanged?.invoke(newValue) ?: stateQueue.add(newValue)
    }

    /**
     * Listens to state changes on a Kaskade flow
     *
     * When mutating the function, it emits all states that are pending in the [stateQueue].
     */
    var onStateChanged: ((state: STATE) -> Unit)? = null
        set(value) {
            field = value

            stateQueue.forEach {
                currentState = it
            }
            stateQueue.clear()
        }

    /**
     * Adds actions to be handled. Used in [create] to build the [Kaskade].
     *
     * @param builder that contains [ACTION] and [Reducer].
     */
    fun addActions(builder: Builder<ACTION, STATE>.() -> Unit) {
        val eventBuilder = Builder<ACTION, STATE>()
        eventBuilder.builder()
        actionStateMap.putAll(eventBuilder.transformer)
    }

    /**
     * Processes [action] to ouput a new [STATE].
     *
     * @param [action] to be precessed into a new [STATE].
     */
    fun process(action: ACTION) {
        getReducer(action)?.let { reducer ->
            reducer(action, currentState) { state ->
                if (state !is SingleEvent) {
                    currentState = state
                } else {
                    emitOrEnqueueState(state)
                }
            }
        } ?: throw IncompleteFlowException(action)
    }

    @Suppress("UNCHECKED_CAST")
    internal fun <T : ACTION> getReducer(action: T): Reducer<T, STATE>? =
        actionStateMap[action::class] as? Reducer<T, STATE>

    /**
     * Clears reference to [onStateChanged] and [actionStateMap].
     */
    fun unsubscribe() {
        onStateChanged = null
        actionStateMap.clear()
    }

    /**
     * DSL that creates map of [Action] and [Reducer].
     */
    @KaskadeBuilderMarker
    class Builder<ACTION : Action, STATE : State> internal constructor() {

        private val _transformerMap = mutableMapOf<KClass<out ACTION>, Reducer<ACTION, STATE>>()

        internal val transformer get() = _transformerMap.toMap()

        /**
         * Reified version of the [on] method for better syntax in the DSL.
         *
         * @param transformer function that transforms [ActionState] to new [State].
         */
        inline fun <reified T : ACTION> on(noinline transformer: ActionState<T, STATE>.() -> STATE) {
            on(T::class, BlockingReducer(transformer))
        }

        /**
         * Maps the specified class of type [ACTION] to the specified [reducer].
         *
         * @param clazz key to put in the map
         * @param reducer value mapped to [clazz]
         */
        @Suppress("UNCHECKED_CAST")
        fun <T : ACTION> on(clazz: KClass<T>, reducer: Reducer<T, STATE>) {
            _transformerMap[clazz] = reducer as Reducer<ACTION, STATE>
        }
    }

    companion object {

        /**
         * DSL for building the [Kaskade].
         *
         * @param initialState emitted immediately when [onStateChanged] is initialized.
         * @param builder function to create the DSL.
         */
        fun <ACTION : Action, STATE : State> create(
            initialState: STATE,
            builder: Builder<ACTION, STATE>.() -> Unit
        ): Kaskade<ACTION, STATE> {
            val kaskade = Kaskade<ACTION, STATE>(initialState)
            kaskade.addActions(builder)
            return kaskade
        }
    }
}
