package dev.gumil.kaskade.coroutines

import dev.gumil.kaskade.Kaskade
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.yield
import kotlin.test.Test

@ExperimentalCoroutinesApi
internal class DamStateFlowTest {

    private val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
        on<TestAction.Action1> {
            TestState.State1
        }
        on<TestAction.Action2> {
            TestState.State2
        }
        on<TestAction.Action3> {
            TestState.SingleStateEvent
        }
    }

    @Test
    fun stateDamFlow_only_emits_initial_state() {
        val verifier = Verifier<TestState>()
        val numberOfStateChanges = 1

        runTest({
            kaskade.stateDamFlow()
                .take(numberOfStateChanges)
                .verifyCollect(this, verifier.function)
                .join()
        }) {
            verifier.verifyInvokedWithValue(TestState.State1)
        }
    }

    @Test
    fun set_value_on_stateDamFlow_emits_initial_state_and_value() {
        val verifier = Verifier<TestState>()
        val numberOfStateChanges = 2

        runTest({
            val stateFlow = kaskade.stateDamFlow()
            stateFlow
                .take(numberOfStateChanges)
                .verifyCollect(this, verifier.function)
            yield()
            stateFlow.value = TestState.State2
            yield()
        }) {
            verifier.verifyInvokedWithValue(TestState.State1)
            verifier.verifyInvokedWithValue(TestState.State2)
        }
    }

    @Test
    fun stateDamFlow_invoke_latest_emitted_value_before_observing() {
        val verifier = Verifier<TestState>()
        val numberOfStateChanges = 2

        runTest({
            val stateFlow = kaskade.stateDamFlow()
            stateFlow.value = TestState.State1
            stateFlow.value = TestState.State2

            stateFlow
                .take(numberOfStateChanges)
                .verifyCollect(this, verifier.function)
            yield()
            stateFlow.value = TestState.State1
        }) {
            verifier.verifyInvokedWithValue(TestState.State1, 1)
            verifier.verifyInvokedWithValue(TestState.State2, 1)
        }
    }

    @Test
    fun stateDamFlow_should_not_invoke_anything_after_state_flow_is_done() {
        val numberOfStateChanges = 1
        val verifier = Verifier<TestState>()

        runTest({
            val stateFlow = kaskade.stateDamFlow()
            stateFlow
                .take(numberOfStateChanges)
                .verifyCollect(this, verifier.function)
            yield()
            stateFlow.value = TestState.State2
        }) {
            verifier.verifyInvokedWithValue(TestState.State1, 1)
            verifier.verifyInvokedWithValue(TestState.State2, 0)
        }
    }

    @Test
    fun stateDamFlow_should_invoke_values_from_previously_collected_emission() {
        val verifier = Verifier<TestState>()

        runTest({
            val stateFlow = kaskade.stateDamFlow()
            stateFlow
                .take(1)
                .verifyCollect(this, verifier.function)
            yield() // Initial value
            stateFlow
                .take(2)
                .verifyCollect(this, verifier.function)
            yield()
            stateFlow.value = TestState.State2
        }) {
            verifier.verifyInvokedWithValue(TestState.State1, 2)
            verifier.verifyInvokedWithValue(TestState.State2, 1)
        }
    }

    @Test
    fun stateDamFlow_should_invoke_singleEvent_but_not_emit_on_new_collector() {
        val verifier = Verifier<TestState>()

        runTest({
            val stateFlow = kaskade.stateDamFlow()
            stateFlow
                .take(2)
                .verifyCollect(this, verifier.function)
            yield() // Initial value
            stateFlow.value = TestState.SingleStateEvent
            yield() // Single event
            stateFlow
                .take(1)
                .verifyCollect(this, verifier.function)
            stateFlow.value = TestState.State2
            yield()
        }) {
            verifier.verifyInvokedWithValue(TestState.State1, 1)
            verifier.verifyInvokedWithValue(TestState.SingleStateEvent, 1)
            verifier.verifyInvokedWithValue(TestState.State2, 1)
        }
    }

    @Test
    fun stateDamFlow_should_not_invoke_emissions_before_clear() {
        val verifier = Verifier<TestState>()

        runTest({
            val stateFlow = kaskade.stateDamFlow()
            stateFlow.value = TestState.State2
            stateFlow.clear()
            stateFlow
                .take(1)
                .verifyCollect(this, verifier.function)
            stateFlow.value = TestState.SingleStateEvent
        }) {
            verifier.verifyInvokedWithValue(TestState.State1, 0)
            verifier.verifyInvokedWithValue(TestState.SingleStateEvent, 1)
            verifier.verifyInvokedWithValue(TestState.State2, 0)
        }
    }

    @Test
    fun should_emit_initial_and_processed_state() {
        val verifier = Verifier<TestState>()
        val numberOfStateChanges = 2

        runTest({
            val stateFlow = kaskade.stateDamFlow()
            stateFlow
                .take(numberOfStateChanges)
                .verifyCollect(this, verifier.function)
            yield()
            kaskade.process(TestAction.Action2)
            yield()
        }) {
            verifier.verifyInvokedWithValue(TestState.State1)
            verifier.verifyInvokedWithValue(TestState.State2)
        }
    }
}
