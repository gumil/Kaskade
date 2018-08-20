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

class Kaskade<STATE : State, ACTION : Action, EFFECT : Effect> private constructor(
        initialState: STATE
) {

    private val actionResultMap = mutableMapOf<KClass<out ACTION>, (ACTION) -> STATE>()

    private var currentState: STATE by observable(initialState) { _, _, newValue ->
        onStateChanged?.invoke(newValue)
    }

    var onStateChanged: ((state: STATE) -> Unit)? = null

    private val deferredList = mutableListOf<Event<*>>()

    fun addActions(builder: Builder<ACTION, STATE, EFFECT>.() -> Unit) {
        val eventBuilder = Builder<ACTION, STATE, EFFECT>()
        eventBuilder.builder()
        actionResultMap.putAll(eventBuilder.transformer)
    }

    fun processAction(action: ACTION) {
        actionResultMap[action::class]?.invoke(action)?.let {
            currentState = it
        } ?: throw IncompleteFlowException(action)
    }

    fun dispose() {
        deferredList.forEach { it.dispose() }
    }

    class Builder<ACTION: Action, STATE: State, EFFECT: Effect> internal constructor() {

        private val _transformerMap = mutableMapOf<KClass<ACTION>, (ACTION) -> STATE>()

        internal val transformer get() = _transformerMap.toMap()

        inline fun <reified T: ACTION> on(noinline transformer: Reducer<T, STATE, EFFECT>.() -> Unit) {
            on(T::class, transformer)
        }

        @Suppress("UNCHECKED_CAST")
        fun <T: ACTION> on(clazz: KClass<T>, transformerFunction: Reducer<T, STATE, EFFECT>.() -> Unit) {
            transformerFunction(Reducer {
                _transformerMap[clazz as KClass<ACTION>] = it as (ACTION) -> STATE
            })
        }

        class Reducer<ACTION: Action, STATE: State, EFFECT: Effect> internal constructor(
                private val onAssign: ((ACTION) -> STATE) -> Unit
        ){

            fun reduceTo(transform: ((ACTION) -> STATE)) {
                onAssign(transform)
            }

            @Suppress("UNCHECKED_CAST")
            fun <T: EFFECT> withEffect(effect: ((ACTION) -> T)): EffectReducer<STATE, T> {
                return EffectReducer {
                    onAssign(effect + it)
                }
            }

            private operator fun <A, B, C> Function1<A, B>.plus(addend: Function1<B, C>) = { a: A ->
                addend(this(a))
            }
        }

        class EffectReducer<STATE: State, EFFECT : Effect> internal constructor(
                private val onAssign: ((EFFECT) -> STATE) -> Unit
        ) {

            fun reduceTo(effectFunction: (EFFECT) -> STATE) {
                onAssign(effectFunction)
            }

        }
    }

    companion object {
        fun <STATE : State, ACTION : Action, EFFECT : Effect> create(
                initialState: STATE,
                builder: Builder<ACTION, STATE, EFFECT>.() -> Unit
        ): Kaskade<STATE, ACTION, EFFECT> {
            return Kaskade<STATE, ACTION, EFFECT>(initialState).apply {
                addActions(builder)
            }
        }
    }
}