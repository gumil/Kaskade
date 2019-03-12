package io.gumil.kaskade.flow

import io.gumil.kaskade.Kaskade
import io.gumil.kaskade.TestAction
import io.gumil.kaskade.Verifier
import io.gumil.kaskade.TestState
import io.gumil.kaskade.stateDamFlow
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class DamFlowTest {

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
    fun damFlow_when_value_sent_should_invoke_subscribe() {
        val flow = DamFlow<String>()
        val verifier = Verifier<String>()
        val subscriber = verifier.function

        flow.subscribe(subscriber)
        flow.sendValue("hello")

        verifier.verifyInvokedWithValue("hello")
    }

    @Test
    fun damFlow_invoke_latest_emitted_value_before_subscribe() {
        val flow = DamFlow<String>()
        val verifier = Verifier<String>()
        val subscriber = verifier.function

        flow.sendValue("test")
        flow.sendValue("world")
        flow.subscribe(subscriber)
        flow.sendValue("hello")

        verifier.verifyOrder {
            verify("world")
            verify("hello")
        }
    }

    @Test
    fun damFlow_should_not_invoke_anything_after_unsubscribe() {
        val flow = DamFlow<String>()
        val verifier = Verifier<String>()
        val subscriber = verifier.function

        flow.subscribe(subscriber)
        flow.unsubscribe()
        flow.sendValue("hello")

        verifier.verifyInvokedWithValue("hello", 0)
        verifier.verifyNoInvocations()
    }

    @Test
    fun damFlow_should_invoke_last_emitted_after_unsubscribe() {
        val flow = DamFlow<String>()
        val verifier = Verifier<String>()
        val subscriber = verifier.function
        val verifierNoEmissions = Verifier<String>()
        val subscriberNoEmissions = verifierNoEmissions.function

        flow.subscribe(subscriberNoEmissions)
        flow.unsubscribe()
        flow.sendValue("hello")
        flow.subscribe(subscriber)

        verifier.verifyInvokedWithValue("hello")
        verifierNoEmissions.verifyInvokedWithValue("hello", 0)
        verifierNoEmissions.verifyNoInvocations()
    }

    @Test
    fun damFlow_should_not_invoke_last_emitted_after_cleared() {
        val flow = DamFlow<String>()
        val verifier = Verifier<String>()
        val subscriber = verifier.function
        val verifierNoEmissions = Verifier<String>()
        val subscriberNoEmissions = verifierNoEmissions.function

        flow.subscribe(subscriber)
        flow.sendValue("hello")
        flow.clear()
        flow.subscribe(subscriberNoEmissions)

        verifier.verifyInvokedWithValue("hello")
        verifierNoEmissions.verifyInvokedWithValue("hello", 0)
        verifierNoEmissions.verifyNoInvocations()
    }

    @Test
    fun create_flow_from_kaskade_using_extension_function() {
        val stateFlow = kaskade.stateDamFlow()
        val verifier = Verifier<TestState>()
        val subscriber = verifier.function

        stateFlow.subscribe(subscriber)
        kaskade.process(TestAction.Action1)

        verifier.verifyInvokedWithValue(TestState.State1)
    }

    @Test
    fun create_flow_from_kaskade_no_emissions_on_initialized() {
        val stateFlow = kaskade.stateDamFlow()
        val verifier = Verifier<TestState>()
        val subscriber = verifier.function
        stateFlow.subscribe(subscriber)

        verifier.verifyNoInvocations()
    }

    @Test
    fun create_flow_from_kaskade_should_emit_last_state_on_new_subscriber() {
        val stateFlow = kaskade.stateDamFlow()
        val verifier = Verifier<TestState>()
        val subscriber = verifier.function

        kaskade.process(TestAction.Action1)
        stateFlow.unsubscribe()
        stateFlow.subscribe(subscriber)

        verifier.verifyInvokedWithValue(TestState.State1)
    }

    @Test
    fun create_flow_from_kaskade_should_not_emit_excluded_state_on_new_subscriber() {
        val stateFlow = kaskade.stateDamFlow()
        val verifier = Verifier<TestState>()
        val subscriber = verifier.function

        kaskade.process(TestAction.Action1)
        stateFlow.subscribe(subscriber)
        kaskade.process(TestAction.Action3)
        stateFlow.unsubscribe()
        stateFlow.subscribe(subscriber)

        verifier.verifyOrder {
            verify(TestState.State1)
            verify(TestState.SingleStateEvent)
            verify(TestState.State1)
        }
    }

    @Test
    fun should_emit_initial_state_and_processed_state() {
        val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
            on<TestAction.Action1> {
                TestState.State1
            }
            on<TestAction.Action2> {
                TestState.State2
            }
        }

        val verifier = Verifier<TestState>()
        val subscriber = verifier.function

        kaskade.process(TestAction.Action2)

        kaskade.stateDamFlow().subscribe(subscriber)

        verifier.verifyOrder {
            verify(TestState.State1)
            verify(TestState.State2)
        }
    }
}
