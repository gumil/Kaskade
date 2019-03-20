package dev.gumil.kaskade.coroutines

import dev.gumil.kaskade.Action
import dev.gumil.kaskade.Kaskade
import dev.gumil.kaskade.State
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.plus
import kotlin.test.Test
import kotlin.test.assertTrue

internal class CoroutinesKaskadeBuilderTest {

    @Test
    fun create_coroutine_scoped_kaskade_builder() {
        val verifier = Verifier<TestState>()
        val onStateChanged = verifier.function

        runTest({
            val scope = this
            val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
                coroutines(scope) {
                    on<TestAction.Action1> { TestState.State1 }
                    on<TestAction.Action2> { TestState.State2 }
                }
            }

            kaskade.onStateChanged = onStateChanged
            kaskade.process(TestAction.Action1)
            kaskade.process(TestAction.Action2)
        }, {
            verifier.verifyOrder {
                verify(TestState.State1)
                verify(TestState.State1)
                verify(TestState.State2)
            }
        })
    }

    @Test
    fun create_coroutine_kaskade_builder_with_independent_scopes() {
        val verifier = Verifier<TestState>()
        val onStateChanged = verifier.function

        runTest({
            val scope = this
            val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
                coroutines {
                    on<TestAction.Action1>(scope) { TestState.State1 }
                    on<TestAction.Action2>(scope) { TestState.State2 }
                }
            }

            kaskade.onStateChanged = onStateChanged
            kaskade.process(TestAction.Action1)
            kaskade.process(TestAction.Action2)
        }, {
            verifier.verifyOrder {
                verify(TestState.State1)
                verify(TestState.State1)
                verify(TestState.State2)
            }
        })
    }

    @Test
    fun coroutines_with_shared_scope_should_cancel_when_scope_is_cancelled() {
        val verifier = Verifier<TestState>()
        val onStateChanged = verifier.function
        val job = Job()

        runTest({
            var scopedReducer1: ScopedReducer<TestAction.Action1, TestState>? = null
            var scopedReducer2: ScopedReducer<TestAction.Action2, TestState>? = null
            val localScope = this + job
            val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
                coroutines(localScope) {
                    scopedReducer1 = on {
                        delay(1000)
                        throw AssertionError("should not be called")
                    }
                    scopedReducer2 = on {
                        delay(1000)
                        throw AssertionError("should not be called")
                    }
                }
            }

            kaskade.onStateChanged = onStateChanged
            kaskade.process(TestAction.Action1)
            kaskade.process(TestAction.Action2)
            job.cancel()
            scopedReducer1?.await()
            scopedReducer2?.await()
        }, {
            assertTrue(job.isCancelled)
            verifier.verifyInvokedWithValue(TestState.State1, 1)
        })
    }

    @Test
    fun coroutines_with_independent_scopes_should_only_cancel_cancelled_job() {
        val verifier = Verifier<TestState>()
        val onStateChanged = verifier.function

        val job1 = Job()
        val job2 = Job()

        runTest({
            var scopedReducer1: ScopedReducer<TestAction.Action1, TestState>? = null
            var scopedReducer2: ScopedReducer<TestAction.Action2, TestState>? = null
            val localScope1 = this + job1
            val localScope2 = this + job2
            val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
                coroutines {
                    scopedReducer1 = on(localScope1) {
                        delay(1000)
                        throw AssertionError("should not be called")
                    }
                    scopedReducer2 = on(localScope2) {
                        delay(1000)
                        TestState.State2
                    }
                }
            }

            kaskade.onStateChanged = onStateChanged
            kaskade.process(TestAction.Action1)
            kaskade.process(TestAction.Action2)
            job1.cancel()
            scopedReducer1?.await()
            scopedReducer2?.await()
        }, {
            assertTrue(job1.isCancelled)
            verifier.verifyOrder {
                verify(TestState.State1)
                verify(TestState.State2)
            }
        })
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
