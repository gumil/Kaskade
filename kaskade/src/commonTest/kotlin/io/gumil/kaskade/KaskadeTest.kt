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

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class KaskadeTest {

    private val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
        on<TestAction.Action1> {
            assertEquals(TestAction.Action1, action)
            TestState.State1
        }

        on<TestAction.Action2> {
            assertEquals(TestAction.Action2, action)
            TestState.State2
        }

        on<TestAction.Action3> {
            assertEquals(TestAction.Action3, action)
            TestState.State3
        }
    }

    private val stateChanged = TestFunction<TestState>()

    init {
        kaskade.onStateChanged = stateChanged
    }

    @BeforeTest
    fun `should emit initial state`() {
        stateChanged.verifyInvokedWithValue(TestState.State1)
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
        kaskade.process(TestAction.Action1)
        stateChanged.verifyInvokedWithValue(TestState.State1, 2)
    }

    @Test
    fun `action2 should emit State2`() {
        kaskade.process(TestAction.Action2)
        stateChanged.verifyInvokedWithValue(TestState.State2)
    }

    @Test
    fun `action3 should emit state3`() {
        kaskade.process(TestAction.Action3)
        stateChanged.verifyInvokedWithValue(TestState.State3)
    }

    @Test
    fun `verify builder transformer`() {
        val transformer: ActionState<TestAction.Action4, TestState>.() -> TestState.State4 = {
            TestState.State4
        }

        val builder = Kaskade.Builder<TestAction, TestState>()
        builder.on(transformer)

        val stateChanged = TestFunction<TestState>()

        val actual = builder.transformer
        assertEquals(1, actual.size)
        assertTrue { actual.containsKey(TestAction.Action4::class) }

        val actualReducer = actual.getValue(TestAction.Action4::class)
        actualReducer(TestAction.Action4, TestState.State4, stateChanged)
        stateChanged.verifyInvokedWithValue(TestState.State4)
    }

    @Test
    fun `builder should be empty on initialized`() {
        val builder = Kaskade.Builder<TestAction, TestState>()
        assertEquals(emptyMap(), builder.transformer)
    }

    @Test
    fun `verify reducer transformer is invokes state changed`() {
        val transformer: ActionState<TestAction.Action4, TestState>.() -> TestState.State4 = {
            TestState.State4
        }
        val reducer = BlockingReducer(transformer)
        val stateChanged = TestFunction<TestState>()

        reducer.invoke(TestAction.Action4, TestState.State4, stateChanged)

        stateChanged.verifyInvokedWithValue(TestState.State4)
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
    fun `emit all states before onStateChaged is set`() {
        kaskade.onStateChanged = null
        kaskade.process(TestAction.Action1)
        kaskade.process(TestAction.Action2)
        kaskade.process(TestAction.Action3)

        val stateChanged = TestFunction<TestState>()

        kaskade.onStateChanged = stateChanged

        stateChanged.verifyOrder {
            verify(TestState.State1)
            verify(TestState.State2)
            verify(TestState.State3)
        }
    }

    @Test
    fun `single event should be emitted but not persisted in current state`() {
        val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
            on<TestAction.Action1> {
                // assert SingleEvent not persisted as current state
                assertEquals(TestState.State1, state)
                assertEquals(
                    ActionState<TestAction.Action1, TestState>(TestAction.Action1, TestState.State1), this
                )
                TestState.State1
            }

            on<TestAction.Action2> {
                TestState.SingleStateEvent
            }
        }

        val stateChanged = TestFunction<TestState>()
        kaskade.onStateChanged = stateChanged

        kaskade.process(TestAction.Action1)
        kaskade.process(TestAction.Action2)
        kaskade.process(TestAction.Action1)

        stateChanged.verifyOrder {
            verify(TestState.State1)
            verify(TestState.State1)
            verify(TestState.SingleStateEvent)
            verify(TestState.State1)
        }
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

        val stateChanged = TestFunction<TestState>()

        kaskade.process(TestAction.Action2)

        kaskade.onStateChanged = stateChanged

        stateChanged.verifyOrder {
            verify(TestState.State1)
            verify(TestState.State2)
        }
    }
}
