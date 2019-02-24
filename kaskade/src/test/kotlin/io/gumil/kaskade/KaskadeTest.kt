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

import kotlin.reflect.KClass
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

internal class KaskadeTest {

    private val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
        on<TestAction.Action1> {
            TestState.State1
        }

        on<TestAction.Action2> {
            TestState.State2
        }

        on<TestAction.Action3> {
            TestState.State3
        }
    }

    @BeforeTest
    fun `should emit initial state`() {
        kaskade.onStateChanged = {
            assertEquals(TestState.State1, it)
        }
    }

    @Test
    fun `action not in Kaskade should throw exception`() {
        assertFailsWith(IncompleteFlowException::class) {
            kaskade.process(TestAction.Action4)
        }
    }

    @Test
    fun `process action after unsubscribing should throw exception`() {
        kaskade.unsubscribe()
        assertFailsWith(IncompleteFlowException::class) {
            kaskade.process(TestAction.Action1)
        }
    }

    @Test
    fun `action1 should emit State1`() {
        kaskade.onStateChanged = {
            assertEquals(TestState.State1, it)
        }
        kaskade.process(TestAction.Action1)
    }

    @Test
    fun `action2 should emit State2`() {
        kaskade.onStateChanged = {
            assertEquals(TestState.State2, it)
        }
        kaskade.process(TestAction.Action2)
    }

    @Test
    fun `action3 should emit state3`() {
        kaskade.onStateChanged = {
            assertEquals(TestState.State3, it)
        }
        kaskade.process(TestAction.Action3)
    }

    @Test
    fun `verify builder transformer`() {
        val transformer: ActionState<TestAction.Action4, TestState>.() -> TestState.State4 = {
            TestState.State4
        }

        val builder = Kaskade.Builder<TestAction, TestState>()
        builder.on(transformer)

        val expected = mapOf<KClass<out TestAction>, Reducer<out TestAction, TestState>>(
            TestAction.Action4::class to BlockingReducer(transformer)
        )

        assertEquals(expected, builder.transformer)
    }

    @Test
    fun `builder should be empty on initialized`() {
        val builder = Kaskade.Builder<TestAction, TestState>()
        assertEquals(emptyMap(), builder.transformer)
    }

    @Test
    fun `verify reducer transformer is invoked`() {
        val transformer: ActionState<TestAction.Action4, TestState>.() -> TestState.State4 = {
            TestState.State4
        }
        val reducer = BlockingReducer(transformer)

        reducer.invoke(TestAction.Action4, TestState.State4) {
            assertEquals(TestState.State4, it)
        }
    }

    @Test
    fun `get reducer with Action1 should return correct reducer`() {
        val expected = BlockingReducer<TestAction, TestState> { TestState.State1 }
        val actual = kaskade.getReducer(TestAction.Action1) as BlockingReducer
        assertEquals(
            expected.getState(TestAction.Action1, TestState.State1),
            actual.getState(TestAction.Action1, TestState.State1)
        )
    }

    @Test
    fun `get reducer with action not registered should return null`() {
        assertNull(kaskade.getReducer(TestAction.Action4))
    }

    @Test
    fun `emit all states before onStateChaged is null`() {
        kaskade.onStateChanged = null
        kaskade.process(TestAction.Action1)
        kaskade.process(TestAction.Action2)
        kaskade.process(TestAction.Action3)

        val states = arrayOf(
            TestState.State1,
            TestState.State2,
            TestState.State3
        )

        var index = 0
        kaskade.onStateChanged = {
            assertEquals(states[index++], it)
        }
    }

    @Test
    fun `single event should be emitted but not persisted in current state`() {
        val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
            on<TestAction.Action1> {
                assertEquals(TestState.State1, state)
                TestState.State1
            }

            on<TestAction.Action2> {
                TestState.SingleStateEvent
            }
        }

        var counter = 0
        kaskade.onStateChanged = { state ->
            when (counter++) {
                0, 1, 3 -> assertEquals(TestState.State1, state)
                else -> assertEquals(TestState.SingleStateEvent, state)
            }
        }

        kaskade.process(TestAction.Action1)
        kaskade.process(TestAction.Action2)
        kaskade.process(TestAction.Action1)
    }

    @Test
    fun `should emit initial state and process state`() {
        val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
            on<TestAction.Action1> {
                TestState.State1
            }
            on<TestAction.Action2> {
                TestState.State2
            }
        }

        kaskade.process(TestAction.Action2)

        var counter = 0
        kaskade.onStateChanged = { state ->
            if (counter++ == 0) {
                assertEquals(TestState.State1, state)
            } else {
                assertEquals(TestState.State2, state)
            }
        }
    }
}
