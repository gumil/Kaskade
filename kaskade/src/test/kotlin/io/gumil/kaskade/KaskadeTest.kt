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

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class KaskadeTest {

    private val stateMachine = Kaskade.create<TestState, TestAction>(TestState.State1) {
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
    fun testShouldThrowException() {
        assertFailsWith(IllegalStateException::class) {
            stateMachine.process(TestAction.Action4)
        }
    }

    @Test
    fun testShouldEmitState1() {
        stateMachine.process(TestAction.Action1)
        stateMachine.onStateChanged = {
            assertEquals(TestState.State1, it)
        }
    }

    @Test
    fun testShouldEmitState2() {
        stateMachine.process(TestAction.Action2)
        stateMachine.onStateChanged = {
            assertEquals(TestState.State2, it)
        }
    }

    @Test
    fun testShouldEmitState3() {
        stateMachine.process(TestAction.Action3)
        stateMachine.onStateChanged = {
            assertEquals(TestState.State3, it)
        }
    }
}