package io.gumil.kaskade.flow

import io.gumil.kaskade.*
import java.lang.AssertionError
import kotlin.test.*

internal class DamFlowTest {

    private val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
        on<TestAction.Action1> {
            TestState.State1
        }
        on<TestAction.Action2> {
            TestState.State2
        }
    }

    @BeforeTest
    fun `should emit initial state`() {
        kaskade.onStateChanged = {
            assertEquals(TestState.State1, it)
        }
    }

    @Test
    fun `damFlow when value sent should invoke subscribe`() {
        val flow = DamFlow<String>()
        flow.subscribe {
            assertEquals("hello", it)
        }
        flow.sendValue("hello")
    }

    @Test
    fun `damFlow invoke latest emitted value before subscribe`() {
        val flow = DamFlow<String>()
        var counter = 0

        flow.sendValue("test")
        flow.sendValue("world")
        flow.subscribe {
            if (counter++ == 0) {
                assertEquals("world", it)
                return@subscribe
            }
            assertEquals("hello", it)
        }
        flow.sendValue("hello")
    }

    @Test
    fun `damFlow should not invoke anything after unsubscribe`() {
        val flow = DamFlow<String>()

        val subscription: (String) -> Unit = {
            throw AssertionError("Should not emit anything")
        }

        flow.subscribe(subscription)
        flow.unsubscribe()
        flow.sendValue("hello")
    }

    @Test
    fun `damFlow should invoke last emitted after unsubscribe`() {
        val flow = DamFlow<String>()

        val subscription: (String) -> Unit = {
            throw AssertionError("Should not emit anything")
        }

        flow.subscribe(subscription)
        flow.unsubscribe()
        flow.sendValue("hello")
        flow.subscribe {
            assertEquals("hello", it)
        }
    }

    @Test
    fun `damFlow should not invoke last emitted after cleared`() {
        val flow = DamFlow<String>()

        val subscription: (String) -> Unit = {
            assertEquals("hello", it)
        }

        flow.subscribe(subscription)
        flow.sendValue("hello")
        flow.clear()
        flow.subscribe {
            throw AssertionError("Should not emit anything")
        }
    }

    @Test
    fun `create flow from kaskade using extension function`() {
        val stateFlow = kaskade.stateDamFlow()

        stateFlow.subscribe {
            assertEquals(TestState.State1, it)
        }

        kaskade.process(TestAction.Action1)
    }

    @Test
    fun `create flow from kaskade no emissions on initialized`() {
        val stateFlow = kaskade.stateDamFlow()
        stateFlow.subscribe {
            throw AssertionError("Should not emit anything")
        }
    }

    @Test
    fun `create flow from kaskade with initial action`() {
        val stateFlow = kaskade.stateDamFlow(TestAction.Action1)

        stateFlow.subscribe {
            assertEquals(TestState.State1, it)
        }
    }

    @Test
    fun `create flow from kaskade should emit last state on new subscriber`() {
        val stateFlow = kaskade.stateDamFlow(TestAction.Action1)

        stateFlow.unsubscribe()

        stateFlow.subscribe {
            assertEquals(TestState.State1, it)
        }
    }

    @Test
    fun `create flow from kaskade should not emit excluded state on new subscriber`() {
        val stateFlow = kaskade.stateDamFlow(TestAction.Action1, TestState.State2::class)
        var counter = 0

        val subscription: (TestState) -> Unit = {
            if (counter++ == 1) {
                assertEquals(TestState.State2, it)
            } else {
                assertEquals(TestState.State1, it)
            }
        }
        stateFlow.subscribe(subscription)

        kaskade.process(TestAction.Action2)

        stateFlow.unsubscribe()

        stateFlow.subscribe(subscription)
    }
}