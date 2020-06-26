package dev.gumil.kaskade.flow

import dev.gumil.kaskade.Kaskade
import dev.gumil.kaskade.TestAction
import dev.gumil.kaskade.Verifier
import dev.gumil.kaskade.TestState
import dev.gumil.kaskade.stateEmitter
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

internal class EmitterTest {

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
        val flow = MutableEmitter<String>()
        val verifier = Verifier<String>()
        val subscriber = verifier.function

        flow.subscribe(subscriber)
        flow.sendValue("hello")

        verifier.verifyInvokedWithValue("hello")
    }

    @Test
    fun mutableFlow_only_invoke_values_after_subscribe() {
        val flow = MutableEmitter<String>()
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
        val flow = MutableEmitter<String>()
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
        val stateFlow = kaskade.stateEmitter()
        val verifier = Verifier<TestState>()
        val subscriber = verifier.function

        stateFlow.subscribe(subscriber)
        kaskade.dispatch(TestAction.Action1)

        assertTrue { stateFlow is MutableEmitter<TestState> }
        verifier.verifyInvokedWithValue(TestState.State1)
    }

    @Test
    fun create_flow_from_kaskade_no_emissions_on_initialized() {
        val stateFlow = kaskade.stateEmitter()
        val verifier = Verifier<TestState>()
        val subscriber = verifier.function

        stateFlow.subscribe(subscriber)

        verifier.verifyInvokedWithValue(TestState.State1, 0)
        verifier.verifyNoInvocations()
    }
}
