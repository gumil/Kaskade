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

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
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

    private val mockStateChanged = mockk<(state: TestState) -> Unit>()

    init {
        every { mockStateChanged.invoke(any()) } returns Unit
        kaskade.onStateChanged = mockStateChanged
    }

    @BeforeTest
    fun `should emit initial state`() {
        verify { mockStateChanged.invoke(TestState.State1) }
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
        verify { mockStateChanged.invoke(TestState.State1) }
        confirmVerified(mockStateChanged)
    }

    @Test
    fun `action2 should emit State2`() {
        kaskade.process(TestAction.Action2)
        verify { mockStateChanged.invoke(TestState.State2) }
        confirmVerified(mockStateChanged)
    }

    @Test
    fun `action3 should emit state3`() {
        kaskade.process(TestAction.Action3)
        verify { mockStateChanged.invoke(TestState.State3) }
        confirmVerified(mockStateChanged)
    }

    @Test
    fun `verify builder transformer`() {
        val transformer: ActionState<TestAction.Action4, TestState>.() -> TestState.State4 = {
            TestState.State4
        }

        val builder = Kaskade.Builder<TestAction, TestState>()
        builder.on(transformer)

        val mockOnStateChanged = mockk<(state: TestState) -> Unit>(relaxed = true)

        val actual = builder.transformer
        assertEquals(1, actual.size)
        assertTrue { actual.containsKey(TestAction.Action4::class) }

        val actualReducer = actual.getValue(TestAction.Action4::class)
        actualReducer(TestAction.Action4, TestState.State4, mockOnStateChanged)
        verify { mockOnStateChanged.invoke(TestState.State4) }
        confirmVerified(mockOnStateChanged)
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
        val mockOnStateChanged = mockk<(state: TestState) -> Unit>(relaxed = true)

        reducer.invoke(TestAction.Action4, TestState.State4, mockOnStateChanged)

        verify { mockOnStateChanged.invoke(TestState.State4) }
        confirmVerified(mockOnStateChanged)
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
        val mockOnStateChanged = mockk<(state: TestState) -> Unit>()
        every { mockOnStateChanged.invoke(any()) } returns Unit

        kaskade.onStateChanged = mockOnStateChanged

        verifyOrder {
            mockOnStateChanged.invoke(TestState.State1)
            mockOnStateChanged.invoke(TestState.State2)
            mockOnStateChanged.invoke(TestState.State3)
        }
        confirmVerified(mockOnStateChanged)
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

        val mockOnStateChanged = mockk<(state: TestState) -> Unit>()
        every { mockOnStateChanged.invoke(any()) } returns Unit
        kaskade.onStateChanged = mockOnStateChanged

        kaskade.process(TestAction.Action1)
        kaskade.process(TestAction.Action2)
        kaskade.process(TestAction.Action1)

        verifyOrder {
            mockOnStateChanged.invoke(TestState.State1)
            mockOnStateChanged.invoke(TestState.State1)
            mockOnStateChanged.invoke(TestState.SingleStateEvent)
            mockOnStateChanged.invoke(TestState.State1)
        }
        confirmVerified(mockOnStateChanged)
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

        val mockOnStateChanged = mockk<(state: TestState) -> Unit>()
        every { mockOnStateChanged.invoke(any()) } returns Unit

        kaskade.process(TestAction.Action2)

        kaskade.onStateChanged = mockOnStateChanged

        verifyOrder {
            mockOnStateChanged.invoke(TestState.State1)
            mockOnStateChanged.invoke(TestState.State2)
        }

        confirmVerified(mockOnStateChanged)
    }
}
