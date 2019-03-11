package io.gumil.kaskade.flow

import io.gumil.kaskade.Kaskade
import io.gumil.kaskade.TestAction
import io.gumil.kaskade.TestFunction
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

    private val stateChanged = TestFunction<TestState>()

    init {
        kaskade.onStateChanged = stateChanged
    }

    @BeforeTest
    fun should_emit_initial_state() {
        stateChanged.verifyInvokedWithValue(TestState.State1)
    }

    @Test
    fun damFlow_when_value_sent_should_invoke_subscribe() {
        val flow = DamFlow<String>()
        val subscriber = TestFunction<String>()

        flow.subscribe(subscriber)
        flow.sendValue("hello")

        subscriber.verifyInvokedWithValue("hello")
    }

    @Test
    fun damFlow_invoke_latest_emitted_value_before_subscribe() {
        val flow = DamFlow<String>()
        val subscriber = TestFunction<String>()

        flow.sendValue("test")
        flow.sendValue("world")
        flow.subscribe(subscriber)
        flow.sendValue("hello")

        subscriber.verifyOrder {
            verify("world")
            verify("hello")
        }
    }

    @Test
    fun damFlow_should_not_invoke_anything_after_unsubscribe() {
        val flow = DamFlow<String>()
        val subscriber = TestFunction<String>()

        flow.subscribe(subscriber)
        flow.unsubscribe()
        flow.sendValue("hello")

        subscriber.verifyInvokedWithValue("hello", 0)
        subscriber.verifyNoInvocations()
    }

    @Test
    fun damFlow_should_invoke_last_emitted_after_unsubscribe() {
        val flow = DamFlow<String>()
        val subscriber = TestFunction<String>()
        val subscriberNoEmissions = TestFunction<String>()

        flow.subscribe(subscriberNoEmissions)
        flow.unsubscribe()
        flow.sendValue("hello")
        flow.subscribe(subscriber)

        subscriber.verifyInvokedWithValue("hello")
        subscriberNoEmissions.verifyInvokedWithValue("hello", 0)
        subscriberNoEmissions.verifyNoInvocations()
    }

    @Test
    fun damFlow_should_not_invoke_last_emitted_after_cleared() {
        val flow = DamFlow<String>()
        val subscriber = TestFunction<String>()
        val subscriberNoEmissions = TestFunction<String>()

        flow.subscribe(subscriber)
        flow.sendValue("hello")
        flow.clear()
        flow.subscribe(subscriberNoEmissions)

        subscriber.verifyInvokedWithValue("hello")
        subscriberNoEmissions.verifyInvokedWithValue("hello", 0)
        subscriberNoEmissions.verifyNoInvocations()
    }

    @Test
    fun create_flow_from_kaskade_using_extension_function() {
        val stateFlow = kaskade.stateDamFlow()
        val subscriber = TestFunction<TestState>()

        stateFlow.subscribe(subscriber)
        kaskade.process(TestAction.Action1)

        subscriber.verifyInvokedWithValue(TestState.State1)
    }

    @Test
    fun create_flow_from_kaskade_no_emissions_on_initialized() {
        val stateFlow = kaskade.stateDamFlow()
        val subscriber = TestFunction<TestState>()
        stateFlow.subscribe(subscriber)

        subscriber.verifyNoInvocations()
    }

    @Test
    fun create_flow_from_kaskade_should_emit_last_state_on_new_subscriber() {
        val stateFlow = kaskade.stateDamFlow()
        val subscriber = TestFunction<TestState>()

        kaskade.process(TestAction.Action1)
        stateFlow.unsubscribe()
        stateFlow.subscribe(subscriber)

        subscriber.verifyInvokedWithValue(TestState.State1)
    }

    @Test
    fun create_flow_from_kaskade_should_not_emit_excluded_state_on_new_subscriber() {
        val stateFlow = kaskade.stateDamFlow()
        val subscriber = TestFunction<TestState>()

        kaskade.process(TestAction.Action1)
        stateFlow.subscribe(subscriber)
        kaskade.process(TestAction.Action3)
        stateFlow.unsubscribe()
        stateFlow.subscribe(subscriber)

        subscriber.verifyOrder {
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

        val subscriber = TestFunction<TestState>()

        kaskade.process(TestAction.Action2)

        kaskade.stateDamFlow().subscribe(subscriber)

        subscriber.verifyOrder {
            verify(TestState.State1)
            verify(TestState.State2)
        }
    }
}
