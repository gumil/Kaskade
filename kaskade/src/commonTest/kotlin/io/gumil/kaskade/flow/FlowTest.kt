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
    fun `should emit initial state`() {
        stateChanged.verifyInvokedWithValue(TestState.State1)
    }

    @Test
    fun `mutableFlow when value sent should invoke subscribe`() {
        val flow = MutableFlow<String>()
        val subscriber = TestFunction<String>()

        flow.subscribe(subscriber)
        flow.sendValue("hello")

        subscriber.verifyInvokedWithValue("hello")
    }

    @Test
    fun `mutableFlow only invoke values after subscribe`() {
        val flow = MutableFlow<String>()
        val subscriber = TestFunction<String>()

        flow.sendValue("world")
        flow.subscribe(subscriber)
        flow.sendValue("hello")

        subscriber.verifyInvokedWithValue("world", 0)
        subscriber.verifyInvokedWithValue("hello")
    }

    @Test
    fun `mutableFlow should not invoke anything after unsubscribe`() {
        val flow = MutableFlow<String>()
        val subscriber = TestFunction<String>()

        flow.subscribe(subscriber)
        flow.unsubscribe()
        flow.sendValue("hello")

        subscriber.verifyInvokedWithValue("hello", 0)
        subscriber.verifyNoInvocations()
    }

    @Test
    fun `create flow from kaskade using extension function`() {
        val stateFlow = kaskade.stateFlow()
        val subscriber = TestFunction<TestState>()

        stateFlow.subscribe(subscriber)
        kaskade.process(TestAction.Action1)

        assertTrue { stateFlow is MutableFlow<TestState> }
        subscriber.verifyInvokedWithValue(TestState.State1)
    }

    @Test
    fun `create flow from kaskade no emissions on initialized`() {
        val stateFlow = kaskade.stateFlow()
        val subscriber = TestFunction<TestState>()

        stateFlow.subscribe(subscriber)

        subscriber.verifyInvokedWithValue(TestState.State1, 0)
        subscriber.verifyNoInvocations()
    }
}
