package dev.gumil.kaskade.coroutines

import dev.gumil.kaskade.Kaskade
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
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
            kaskade.dispatch(TestAction.Action1)
            kaskade.dispatch(TestAction.Action2)
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
            kaskade.dispatch(TestAction.Action1)
            kaskade.dispatch(TestAction.Action2)
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
            kaskade.dispatch(TestAction.Action1)
            kaskade.dispatch(TestAction.Action2)
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
            kaskade.dispatch(TestAction.Action1)
            kaskade.dispatch(TestAction.Action2)
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

    @Test
    fun create_coroutine_flow_kaskade_builder() {
        val verifier = Verifier<TestState>()
        val onStateChanged = verifier.function

        runTest({
            val scope = this
            val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
                coroutines(scope) {
                    onFlow<TestAction.Action1> { map { TestState.State1 } }
                    onFlow<TestAction.Action2> { map { TestState.State2 } }
                }
            }

            kaskade.onStateChanged = onStateChanged
            kaskade.dispatch(TestAction.Action1)
            kaskade.dispatch(TestAction.Action2)
        }, {
            verifier.verifyOrder {
                verify(TestState.State1)
                verify(TestState.State1)
                verify(TestState.State2)
            }
        })
    }

    @Test
    fun create_coroutine_flow_kaskade_builder_with_independent_scopes() {
        val verifier = Verifier<TestState>()
        val onStateChanged = verifier.function

        runTest({
            val scope = this
            val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
                coroutines {
                    onFlow<TestAction.Action1>(scope) { map { TestState.State1 } }
                    onFlow<TestAction.Action2>(scope) { map { TestState.State2 } }
                }
            }

            kaskade.onStateChanged = onStateChanged
            kaskade.dispatch(TestAction.Action1)
            kaskade.dispatch(TestAction.Action2)
        }, {
            verifier.verifyOrder {
                verify(TestState.State1)
                verify(TestState.State1)
                verify(TestState.State2)
            }
        })
    }
}
