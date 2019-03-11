package io.gumil.kaskade.flow

import io.gumil.kaskade.Kaskade
import io.gumil.kaskade.TestAction
import io.gumil.kaskade.TestFunction
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

    private val stateChanged = TestFunction<TestState>()

    init {
        kaskade.onStateChanged = stateChanged
    }

    @BeforeTest
    fun should_emit_initial_state() {
        stateChanged.verifyInvokedWithValue(TestState.State1)
    }

    @Test
    fun mutableFlow_when_value_sent_should_invoke_subscribe() {
        val flow = MutableFlow<String>()
        val subscriber = TestFunction<String>()

        flow.subscribe(subscriber)
        flow.sendValue("hello")

        subscriber.verifyInvokedWithValue("hello")
    }

    @Test
    fun mutableFlow_only_invoke_values_after_subscribe() {
        val flow = MutableFlow<String>()
        val subscriber = TestFunction<String>()

        flow.sendValue("world")
        flow.subscribe(subscriber)
        flow.sendValue("hello")

        subscriber.verifyInvokedWithValue("world", 0)
        subscriber.verifyInvokedWithValue("hello")
    }

    @Test
    fun mutableFlow_should_not_invoke_anything_after_unsubscribe() {
        val flow = MutableFlow<String>()
        val subscriber = TestFunction<String>()

        flow.subscribe(subscriber)
        flow.unsubscribe()
        flow.sendValue("hello")

        subscriber.verifyInvokedWithValue("hello", 0)
        subscriber.verifyNoInvocations()
    }

    @Test
    fun create_flow_from_kaskade_using_extension_function() {
        val stateFlow = kaskade.stateFlow()
        val subscriber = TestFunction<TestState>()

        stateFlow.subscribe(subscriber)
        kaskade.process(TestAction.Action1)

        assertTrue { stateFlow is MutableFlow<TestState> }
        subscriber.verifyInvokedWithValue(TestState.State1)
    }

    @Test
    fun create_flow_from_kaskade_no_emissions_on_initialized() {
        val stateFlow = kaskade.stateFlow()
        val subscriber = TestFunction<TestState>()

        stateFlow.subscribe(subscriber)

        subscriber.verifyInvokedWithValue(TestState.State1, 0)
        subscriber.verifyNoInvocations()
    }
}
