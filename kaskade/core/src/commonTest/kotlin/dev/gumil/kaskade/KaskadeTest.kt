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

    private val stateChangeVerifier = Verifier<TestState>()
    private val stateChanged = stateChangeVerifier.function

    init {
        kaskade.onStateChanged = stateChanged
    }

    @BeforeTest
    fun should_emit_initial_state() {
        stateChangeVerifier.verifyInvokedWithValue(TestState.State1)
    }

    @Test
    fun action_not_in_Kaskade_should_throw_exception() {
        assertFailsWith(IncompleteFlowException::class) {
            kaskade.process(TestAction.Action4)
        }
    }

    @Test
    fun process_action_after_unsubscribing_should_throw_exception() {
        kaskade.unsubscribe()
        assertFailsWith(IncompleteFlowException::class) {
            kaskade.process(TestAction.Action1)
        }
    }

    @Test
    fun action1_should_emit_State1() {
        kaskade.process(TestAction.Action1)
        stateChangeVerifier.verifyInvokedWithValue(TestState.State1, 2)
    }

    @Test
    fun action2_should_emit_State2() {
        kaskade.process(TestAction.Action2)
        stateChangeVerifier.verifyInvokedWithValue(TestState.State2)
    }

    @Test
    fun action3_should_emit_State3() {
        kaskade.process(TestAction.Action3)
        stateChangeVerifier.verifyInvokedWithValue(TestState.State3)
    }

    @Test
    fun verify_builder_transformer() {
        val transformer: ActionState<TestAction.Action4, TestState>.() -> TestState.State4 = {
            TestState.State4
        }

        val builder = Kaskade.Builder<TestAction, TestState>()
        builder.on(transformer)

        val verifier = Verifier<TestState>()
        val stateChanged = verifier.function

        val actual = builder.transformer
        assertEquals(1, actual.size)
        assertTrue { actual.containsKey(TestAction.Action4::class) }

        val actualReducer = actual.getValue(TestAction.Action4::class)
        actualReducer(TestAction.Action4, TestState.State4, stateChanged)
        verifier.verifyInvokedWithValue(TestState.State4)
    }

    @Test
    fun builder_should_be_empty_on_initialized() {
        val builder = Kaskade.Builder<TestAction, TestState>()
        assertEquals(emptyMap(), builder.transformer)
    }

    @Test
    fun verify_reducer_transformer_is_invokes_state_changed() {
        val transformer: ActionState<TestAction.Action4, TestState>.() -> TestState.State4 = {
            TestState.State4
        }
        val reducer = BlockingReducer(transformer)
        val verifier = Verifier<TestState>()
        val stateChanged = verifier.function

        reducer.invoke(TestAction.Action4, TestState.State4, stateChanged)

        verifier.verifyInvokedWithValue(TestState.State4)
    }

    @Test
    fun get_reducer_with_Action1_should_return_correct_reducer() {
        val expected = BlockingReducer<TestAction, TestState> { TestState.State1 }
        val actual = kaskade.getReducer(TestAction.Action1) as BlockingReducer
        assertEquals(
            expected.getState(TestAction.Action1, TestState.State1),
            actual.getState(TestAction.Action1, TestState.State1)
        )
    }

    @Test
    fun get_reducer_with_action_not_registered_should_return_null() {
        assertNull(kaskade.getReducer(TestAction.Action4))
    }

    @Test
    fun emit_all_states_before_onStateChanged_is_set() {
        kaskade.onStateChanged = null
        kaskade.process(TestAction.Action1)
        kaskade.process(TestAction.Action2)
        kaskade.process(TestAction.Action3)

        val verifier = Verifier<TestState>()
        val stateChanged = verifier.function

        kaskade.onStateChanged = stateChanged

        verifier.verifyOrder {
            verify(TestState.State1)
            verify(TestState.State2)
            verify(TestState.State3)
        }
    }

    @Test
    fun single_event_should_be_emitted_but_not_persisted_in_current_state() {
        val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
            on<TestAction.Action1> {
                // assert SingleEvent not persisted as current state
                assertEquals(TestState.State1, currentState)
                assertEquals(
                    ActionState<TestAction.Action1, TestState>(TestAction.Action1, TestState.State1), this
                )
                TestState.State1
            }

            on<TestAction.Action2> {
                TestState.SingleStateEvent
            }
        }

        val verifier = Verifier<TestState>()
        val stateChanged = verifier.function
        kaskade.onStateChanged = stateChanged

        kaskade.process(TestAction.Action1)
        kaskade.process(TestAction.Action2)
        kaskade.process(TestAction.Action1)

        verifier.verifyOrder {
            verify(TestState.State1)
            verify(TestState.State1)
            verify(TestState.SingleStateEvent)
            verify(TestState.State1)
        }
    }

    @Test
    fun should_emit_initial_state_and_process_state() {
        val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
            on<TestAction.Action1> {
                TestState.State1
            }
            on<TestAction.Action2> {
                TestState.State2
            }
        }

        val verifier = Verifier<TestState>()
        val stateChanged = verifier.function

        kaskade.process(TestAction.Action2)

        kaskade.onStateChanged = stateChanged

        verifier.verifyOrder {
            verify(TestState.State1)
            verify(TestState.State2)
        }
    }
}
