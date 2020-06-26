package dev.gumil.kaskade.coroutines

import dev.gumil.kaskade.Kaskade
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.yield
import kotlin.test.Test

@ExperimentalCoroutinesApi
internal class StateFlowTest {

    private val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
        on<TestAction.Action1> {
            TestState.State1
        }
        on<TestAction.Action2> {
            TestState.State2
        }
    }

    @Test
    fun stateFlow_only_emits_initial_state() {
        val verifier = Verifier<TestState>()
        val numberOfStateChanges = 1

        runTest({
            kaskade.stateFlow()
                .take(numberOfStateChanges)
                .verifyCollect(this, verifier.function)
                .join()
        }) {
            verifier.verifyInvokedWithValue(TestState.State1)
        }
    }

    @Test
    fun set_value_on_stateflow_should_only_emit_value_that_was_set() {
        val verifier = Verifier<TestState>()
        val numberOfStateChanges = 1

        runTest({
            val stateFlow = kaskade.stateFlow() as MutableStateFlow
            val job = stateFlow
                .take(numberOfStateChanges)
                .verifyCollect(this, verifier.function)

            stateFlow.value = TestState.State2

            job.join()
        }) {
            verifier.verifyInvokedWithValue(TestState.State2)
        }
    }

    @Test
    fun stateFlow_invoke_latest_emitted_value_before_observing() {
        val verifier = Verifier<TestState>()
        val numberOfStateChanges = 2

        runTest({
            val stateFlow = kaskade.stateFlow() as MutableStateFlow
            stateFlow.value = TestState.State1
            stateFlow.value = TestState.State2
            stateFlow.value = TestState.State1

            stateFlow
                .take(numberOfStateChanges)
                .verifyCollect(this, verifier.function)

            yield() // Initial value

            stateFlow.value = TestState.State2
        }) {
            verifier.verifyInvokedWithValue(TestState.State1)
            verifier.verifyInvokedWithValue(TestState.State2)
        }
    }

    @Test
    fun stateFlow_should_not_invoke_anything_after_state_flow_is_done() {
        val numberOfStateChanges = 1
        val verifier = Verifier<TestState>()

        runTest({
            val stateFlow = kaskade.stateFlow() as MutableStateFlow
            stateFlow
                .take(numberOfStateChanges)
                .verifyCollect(this, verifier.function)
            yield() // Initial value
            stateFlow.value = TestState.State2
        }) {
            verifier.verifyInvokedWithValue(TestState.State1, 1)
            verifier.verifyInvokedWithValue(TestState.State2, 0)
        }
    }

    @Test
    fun stateFlow_should_invoke_last_emitted_after_first_collect_is_done() {
        val numberOfStateChanges = 1
        val verifier = Verifier<TestState>()

        runTest({
            val stateFlow = kaskade.stateFlow() as MutableStateFlow
            stateFlow
                .take(numberOfStateChanges)
                .verifyCollect(this, verifier.function)
            yield() // Initial value
            stateFlow.value = TestState.State2
            stateFlow
                .take(numberOfStateChanges)
                .verifyCollect(this, verifier.function)
            yield()
        }) {
            verifier.verifyInvokedWithValue(TestState.State1, 1)
            verifier.verifyInvokedWithValue(TestState.State2, 1)
        }
    }

    @Test
    fun should_emit_initial_and_processed_state() {
        val verifier = Verifier<TestState>()
        val numberOfStateChanges = 2

        runTest({
            val stateFlow = kaskade.stateFlow()
            stateFlow
                .take(numberOfStateChanges)
                .verifyCollect(this, verifier.function)
            yield()
            kaskade.dispatch(TestAction.Action2)
            yield()
        }) {
            verifier.verifyInvokedWithValue(TestState.State1)
            verifier.verifyInvokedWithValue(TestState.State2)
        }
    }
}
