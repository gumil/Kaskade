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

import kotlin.test.assertEquals
import kotlin.test.assertTrue

sealed class TestState : State {
    object State1 : TestState()
    object State2 : TestState()
    object State3 : TestState()
    object State4 : TestState()
    object SingleStateEvent : TestState(), SingleEvent
}

sealed class TestAction : Action {
    object Action1 : TestAction()
    object Action2 : TestAction()
    object Action3 : TestAction()
    object Action4 : TestAction()
}

internal class Verifier<T> {

    val function: (T) -> Unit = {
        invokedValues.add(it)
    }

    private var invokedValues = mutableListOf<T>()

    fun verifyInvokedWithValue(value: T, times: Int = 1) {
        assertEquals(invokedValues.filter { it == value }.size, times)
    }

    fun verifyNoInvocations() {
        assertTrue { invokedValues.isEmpty() }
    }

    fun verifyOrder(verifyBuilder: OrderedBuilder<T>.() -> Unit) {
        val orderedBuilder = OrderedBuilder<T>()
        verifyBuilder(orderedBuilder)
        orderedBuilder.values.forEachIndexed { index, value ->
            assertEquals(invokedValues[index], value)
        }
    }

    internal class OrderedBuilder<T> {
        private val _values = mutableListOf<T>()

        val values: List<T> = _values

        fun verify(value: T) {
            _values.add(value)
        }
    }
}
