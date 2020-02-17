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

package dev.gumil.kaskade

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

    private val actionWatchers: MutableList<(ACTION) -> Unit> = mutableListOf()

    private val stateWatchers: MutableList<(STATE) -> Unit> = mutableListOf()

    private var currentState: STATE by observable(initialState) { _, _, newValue ->
        emitOrEnqueueState(newValue)
    }

    private fun emitOrEnqueueState(newValue: STATE) {
        onStateChanged?.invoke(newValue) ?: stateQueue.add(newValue)
    }

    /**
     * Listens to state changes on a Kaskade flow
     *
     * When mutating the function, it emits all states that are pending in the state queue.
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
     * Processes [action] to output a new [STATE].
     *
     * @param [action] to be processed into a new [STATE].
     */
    fun dispatch(action: ACTION) {
        if (actionStateMap.isEmpty()) {
            throw IllegalStateException("No actions can be processed. Kaskade might be unsubscribed at this point.")
        }

        getReducer(action)?.let { reducer ->
            reducer(action, currentState) { state ->
                actionWatchers.forEach { it.invoke(action) }
                stateWatchers.forEach { it.invoke(state) }
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
     * Watches every succeeding action that is dispatched.
     *
     * @param [watcher] listens to actions.
     */
    fun addActionWatcher(watcher: (ACTION) -> Unit) {
        actionWatchers.add(watcher)
    }

    /**
     * Watches every succeeding state that is emitted unlike with [onStateChanged] that
     * enqueues unhandled state emissions and emits whenever [onStateChanged] is assigned to a
     * non-null value.
     *
     * @param [watcher] listens to states.
     */
    fun addStateWatcher(watcher: (STATE) -> Unit) {
        stateWatchers.add(watcher)
    }

    /**
     * Removes associated [watcher].
     *
     * @param [watcher] to be removed.
     */
    fun removeActionWatcher(watcher: (ACTION) -> Unit) {
        actionWatchers.remove(watcher)
    }

    /**
     * Removes associated [watcher].
     *
     * @param [watcher] to be removed.
     */
    fun removeStateWatcher(watcher: (STATE) -> Unit) {
        stateWatchers.remove(watcher)
    }

    /**
     * Clears action and state bindings.
     */
    fun unsubscribe() {
        onStateChanged = null
        actionStateMap.clear()
        actionWatchers.clear()
        stateWatchers.clear()
    }

    /**
     * DSL that creates map of [Action] and [Reducer].
     */
    @KaskadeBuilderMarker
    class Builder<ACTION : Action, STATE : State> internal constructor() {

        private val _transformerMap = mutableMapOf<KClass<out ACTION>, Reducer<ACTION, STATE>>()

        private var _actionWatcher: ((ACTION) -> Unit)? = null

        private var _stateWatcher: ((STATE) -> Unit)? = null

        internal val transformer get() = _transformerMap.toMap()

        internal val actionWatcher get() = _actionWatcher

        internal val stateWatcher get() = _stateWatcher

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

        /**
         * DSL to watch every action that is dispatched.
         *
         * @param [watcher] listens to actions.
         */
        fun watchActions(watcher: (ACTION) -> Unit) {
            _actionWatcher = watcher
        }

        /**
         * DSL to watch every state that is emitted.
         *
         * @param [watcher] listens to states.
         */
        fun watchStates(watcher: (STATE) -> Unit) {
            _stateWatcher = watcher
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
            val eventBuilder = Builder<ACTION, STATE>()

            eventBuilder.builder()

            kaskade.actionStateMap.putAll(eventBuilder.transformer)
            eventBuilder.actionWatcher?.let(kaskade::addActionWatcher)
            eventBuilder.stateWatcher?.let(kaskade::addStateWatcher)
            eventBuilder.stateWatcher?.invoke(initialState)

            return kaskade
        }
    }
}
