package io.gumil.kaskade.flow

import io.gumil.kaskade.Kaskade
import io.gumil.kaskade.TestAction
import io.gumil.kaskade.Verifier
import io.gumil.kaskade.TestState
import io.gumil.kaskade.stateFlow
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

internal class FlowTest {

    private val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
        on<TestAction.Action1> {
            TestState.State1
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
    fun mutableFlow_when_value_sent_should_invoke_subscribe() {
        val flow = MutableFlow<String>()
        val verifier = Verifier<String>()
        val subscriber = verifier.function

        flow.subscribe(subscriber)
        flow.sendValue("hello")

        verifier.verifyInvokedWithValue("hello")
    }

    @Test
    fun mutableFlow_only_invoke_values_after_subscribe() {
        val flow = MutableFlow<String>()
        val verifier = Verifier<String>()
        val subscriber = verifier.function

        flow.sendValue("world")
        flow.subscribe(subscriber)
        flow.sendValue("hello")

        verifier.verifyInvokedWithValue("world", 0)
        verifier.verifyInvokedWithValue("hello")
    }

    @Test
    fun mutableFlow_should_not_invoke_anything_after_unsubscribe() {
        val flow = MutableFlow<String>()
        val verifier = Verifier<String>()
        val subscriber = verifier.function

        flow.subscribe(subscriber)
        flow.unsubscribe()
        flow.sendValue("hello")

        verifier.verifyInvokedWithValue("hello", 0)
        verifier.verifyNoInvocations()
    }

    @Test
    fun create_flow_from_kaskade_using_extension_function() {
        val stateFlow = kaskade.stateFlow()
        val verifier = Verifier<TestState>()
        val subscriber = verifier.function

        stateFlow.subscribe(subscriber)
        kaskade.process(TestAction.Action1)

        assertTrue { stateFlow is MutableFlow<TestState> }
        verifier.verifyInvokedWithValue(TestState.State1)
    }

    @Test
    fun create_flow_from_kaskade_no_emissions_on_initialized() {
        val stateFlow = kaskade.stateFlow()
        val verifier = Verifier<TestState>()
        val subscriber = verifier.function

        stateFlow.subscribe(subscriber)

        verifier.verifyInvokedWithValue(TestState.State1, 0)
        verifier.verifyNoInvocations()
    }
}