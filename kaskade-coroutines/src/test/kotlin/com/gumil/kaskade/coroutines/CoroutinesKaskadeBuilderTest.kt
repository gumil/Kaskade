package com.gumil.kaskade.coroutines

import io.gumil.kaskade.Action
import io.gumil.kaskade.Kaskade
import io.gumil.kaskade.State
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.plus
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertTrue

internal class CoroutinesKaskadeBuilderTest {

    @Test
    fun `create coroutine scoped kaskade builder`() {
        val mockOnStateChanged = mockk<(state: TestState) -> Unit>(relaxed = true)
        every { mockOnStateChanged.invoke(any()) } returns Unit

        runBlocking {
            val scope = this
            val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
                coroutines(scope) {
                    on<TestAction.Action1> { TestState.State1 }
                    on<TestAction.Action2> { TestState.State2 }
                }
            }

            kaskade.onStateChanged = mockOnStateChanged
            kaskade.process(TestAction.Action1)
            kaskade.process(TestAction.Action2)
        }

        verifyOrder {
            mockOnStateChanged.invoke(TestState.State1)
            mockOnStateChanged.invoke(TestState.State1)
            mockOnStateChanged.invoke(TestState.State2)
        }
        confirmVerified(mockOnStateChanged)
    }

    @Test
    fun `create coroutine kaskade builder with independent scopes`() {
        val mockOnStateChanged = mockk<(state: TestState) -> Unit>(relaxed = true)
        every { mockOnStateChanged.invoke(any()) } returns Unit

        runBlocking {
            val scope = this
            val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
                coroutines {
                    on<TestAction.Action1>(scope) { TestState.State1 }
                    on<TestAction.Action2>(scope) { TestState.State2 }
                }
            }

            kaskade.onStateChanged = mockOnStateChanged
            kaskade.process(TestAction.Action1)
            kaskade.process(TestAction.Action2)
        }

        verifyOrder {
            mockOnStateChanged.invoke(TestState.State1)
            mockOnStateChanged.invoke(TestState.State1)
            mockOnStateChanged.invoke(TestState.State2)
        }
        confirmVerified(mockOnStateChanged)
    }

    @Test
    fun `coroutines with shared scope should cancel when scope is cancelled`() {
        val mockOnStateChanged = mockk<(state: TestState) -> Unit>(relaxed = true)
        every { mockOnStateChanged.invoke(any()) } returns Unit
        val job = Job()

        runBlocking {
            val localScope = this + job
            val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
                coroutines(localScope) {
                    on<TestAction.Action1> {
                        delay(1000)
                        throw AssertionError("should not be called")
                    }
                    on<TestAction.Action2> {
                        delay(1000)
                        throw AssertionError("should not be called")
                    }
                }
            }

            kaskade.onStateChanged = mockOnStateChanged
            kaskade.process(TestAction.Action1)
            kaskade.process(TestAction.Action2)
        }
        job.cancel()

        assertTrue(job.isCancelled)
        verify(exactly = 1) { mockOnStateChanged.invoke(TestState.State1) }
        confirmVerified(mockOnStateChanged)
    }

    @Test
    fun `coroutines with independent scopes should only cancel cancelled job`() {
        val mockOnStateChanged = mockk<(state: TestState) -> Unit>(relaxed = true)
        every { mockOnStateChanged.invoke(any()) } returns Unit

        val job1 = Job()
        val job2 = Job()

        runBlocking {
            val localScope1 = this + job1
            val localScope2 = this + job2
            val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
                coroutines {
                    on<TestAction.Action1>(localScope1) {
                        delay(1000)
                        throw AssertionError("should not be called")
                    }
                    on<TestAction.Action2>(localScope2) {
                        TestState.State2
                    }
                }
            }

            kaskade.onStateChanged = mockOnStateChanged
            kaskade.process(TestAction.Action1)
            kaskade.process(TestAction.Action2)
        }
        job1.cancel()

        assertTrue(job1.isCancelled)
        verifyOrder {
            mockOnStateChanged.invoke(TestState.State1)
            mockOnStateChanged.invoke(TestState.State2)
        }
        confirmVerified(mockOnStateChanged)
    }
}

sealed class TestState : State {
    object State1 : TestState()
    object State2 : TestState()
}

sealed class TestAction : Action {
    object Action1 : TestAction()
    object Action2 : TestAction()
}
