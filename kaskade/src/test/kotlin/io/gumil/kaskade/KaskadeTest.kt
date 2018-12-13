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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class KaskadeTest {

    private val kaskade = Kaskade.create<TestState, TestAction>(TestState.State1) {
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
        kaskade.onStateChanged = {
            assertEquals(TestState.State1, it)
        }
    }

    @Test
    fun `action2 should emit State2`() {
        kaskade.process(TestAction.Action2)
        kaskade.onStateChanged = {
            assertEquals(TestState.State2, it)
        }
    }

    @Test
    fun `action3 should emit state3`() {
        kaskade.process(TestAction.Action3)
        kaskade.onStateChanged = {
            assertEquals(TestState.State3, it)
        }
    }

    @Test
    fun  `verify builder transformer`() {
        val transformer: suspend ActionState<TestAction.Action4, TestState>.() -> TestState.State4 = {
            TestState.State4
        }

        val builder = Kaskade.Builder<TestAction, TestState>(TestState.State1).apply { on(transformer) }

        val expected = mapOf<KClass<out TestAction>, Reducer<out TestAction, TestState>>(
                TestAction.Action4::class to Reducer(TestState.State1, transformer))

        assertEquals(expected, builder.transformer)
    }

    @Test
    fun `builder should be empty on initialized`() {
        val builder = Kaskade.Builder<TestAction, TestState>(TestState.State1)
        assertEquals(emptyMap(), builder.transformer)
    }

    @Test
    fun `verify reducer transformer is invoked`() {
        val transformer: suspend ActionState<TestAction.Action4, TestState>.() -> TestState.State4 = {
            TestState.State4
        }
        val reducer = Reducer(TestState.State1, transformer)

        reducer.invoke(TestAction.Action4, TestState.State4) {
            assertEquals(TestState.State4, it)
        }
    }
}