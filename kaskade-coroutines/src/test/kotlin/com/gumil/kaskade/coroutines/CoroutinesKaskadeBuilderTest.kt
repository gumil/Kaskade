package com.gumil.kaskade.coroutines

import io.gumil.kaskade.Action
import io.gumil.kaskade.Kaskade
import io.gumil.kaskade.State
import kotlinx.coroutines.*
import java.lang.AssertionError
import kotlin.test.*

internal class CoroutinesKaskadeBuilderTest {

    @Test
    fun `create coroutine scoped kaskade builder`() = runBlocking {
        val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
            coroutines(this@runBlocking) {
                on<TestAction.Action1> { TestState.State1 }
            }
        }

        with(TestAction.Action1) {
            kaskade.getReducer(this)!!
                    .asJob(this, TestState.State2) {
                        assertEquals(TestState.State1, it)
                    }.join()
        }
    }

    @Test
    fun `create coroutine kaskade builder with independent scopes`() = runBlocking {
        val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
            coroutines {
                on<TestAction.Action1>(this@runBlocking) { TestState.State1 }
            }
        }

        with(TestAction.Action1) {
            kaskade.getReducer(this)!!
                    .asJob(this, TestState.State2) {
                        assertEquals(TestState.State1, it)
                    }.join()
        }
    }

    @Test
    fun `coroutines with shared scope should cancel when scope is cancelled`() = runBlocking {
        val job = Job()
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

        val job1 = with(TestAction.Action1) {
            kaskade.getReducer(this)!!
                    .asJob(this, TestState.State2) {
                        throw AssertionError("should not be called")
                    }
        }

        val job2 = with(TestAction.Action2) {
            kaskade.getReducer(this)!!
                    .asJob(this, TestState.State2) {
                        throw AssertionError("should not be called")
                    }
        }

        launch { joinAll(job1, job2) }
        job.cancel()
    }

    @Test
    fun `coroutines with independent scopes should only cancel cancelled job`() = runBlocking {
        val job1 = Job()
        val job2 = Job()
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

        val action1Job = with(TestAction.Action1) {
            kaskade.getReducer(this)!!
                    .asJob(this, TestState.State2) {
                        throw AssertionError("should not be called")
                    }
        }

        val action2Job = with(TestAction.Action2) {
            kaskade.getReducer(this)!!
                    .asJob(this, TestState.State2) {
                        assertEquals(TestState.State2, it)
                    }
        }

        launch { action1Job.join()}
        job1.cancel()
        action2Job.join()
    }

    private sealed class TestState : State {
        object State1 : TestState()
        object State2 : TestState()
    }

    private sealed class TestAction : Action {
        object Action1 : TestAction()
        object Action2 : TestAction()
    }
}