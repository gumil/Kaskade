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
    fun `should emit initial state`() {
        stateChanged.verifyInvokedWithValue(TestState.State1)
    }

    @Test
    fun `damFlow when value sent should invoke subscribe`() {
        val flow = DamFlow<String>()
        val subscriber = TestFunction<String>()

        flow.subscribe(subscriber)
        flow.sendValue("hello")

        subscriber.verifyInvokedWithValue("hello")
    }

    @Test
    fun `damFlow invoke latest emitted value before subscribe`() {
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
    fun `damFlow should not invoke anything after unsubscribe`() {
        val flow = DamFlow<String>()
        val subscriber = TestFunction<String>()

        flow.subscribe(subscriber)
        flow.unsubscribe()
        flow.sendValue("hello")

        subscriber.verifyInvokedWithValue("hello", 0)
        subscriber.verifyNoInvocations()
    }

    @Test
    fun `damFlow should invoke last emitted after unsubscribe`() {
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
    fun `damFlow should not invoke last emitted after cleared`() {
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
    fun `create flow from kaskade using extension function`() {
        val stateFlow = kaskade.stateDamFlow()
        val subscriber = TestFunction<TestState>()

        stateFlow.subscribe(subscriber)
        kaskade.process(TestAction.Action1)

        subscriber.verifyInvokedWithValue(TestState.State1)
    }

    @Test
    fun `create flow from kaskade no emissions on initialized`() {
        val stateFlow = kaskade.stateDamFlow()
        val subscriber = TestFunction<TestState>()
        stateFlow.subscribe(subscriber)

        subscriber.verifyNoInvocations()
    }

    @Test
    fun `create flow from kaskade should emit last state on new subscriber`() {
        val stateFlow = kaskade.stateDamFlow()
        val subscriber = TestFunction<TestState>()

        kaskade.process(TestAction.Action1)
        stateFlow.unsubscribe()
        stateFlow.subscribe(subscriber)

        subscriber.verifyInvokedWithValue(TestState.State1)
    }

    @Test
    fun `create flow from kaskade should not emit excluded state on new subscriber`() {
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
    fun `should emit initial state and processed state`() {
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
