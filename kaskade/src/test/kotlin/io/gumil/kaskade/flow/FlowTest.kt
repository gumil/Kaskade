package io.gumil.kaskade.flow

import io.gumil.kaskade.Kaskade
import io.gumil.kaskade.TestAction
import io.gumil.kaskade.TestState
import io.gumil.kaskade.stateFlow
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

internal class FlowTest {

    private val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
        on<TestAction.Action1> {
            TestState.State1
        }
    }

    @BeforeTest
    fun `should emit initial state`() {
        kaskade.onStateChanged = {
            assertEquals(TestState.State1, it)
        }
    }

    @Test
    fun `mutableFlow when value sent should invoke subscribe`() {
        val flow = MutableFlow<String>()
        flow.subscribe {
            assertEquals("hello", it)
        }
        flow.sendValue("hello")
    }

    @Test
    fun `mutableFlow only invoke values after subscribe`() {
        val flow = MutableFlow<String>()
        flow.sendValue("world")
        flow.subscribe {
            assertNotEquals("world", it)
            assertEquals("hello", it)
        }
        flow.sendValue("hello")
    }

    @Test
    fun `mutableFlow should not invoke anything after unsubscribe`() {
        val flow = MutableFlow<String>()

        val subscription: (String) -> Unit = {
            throw AssertionError("Should not emit anything")
        }

        flow.subscribe(subscription)
        flow.unsubscribe()
        flow.sendValue("hello")
    }

    @Test
    fun `create flow from kaskade using extension function`() {
        val stateFlow = kaskade.stateFlow()

        stateFlow.subscribe {
            assertEquals(TestState.State1, it)
        }

        assertTrue { stateFlow is MutableFlow<TestState> }
        kaskade.process(TestAction.Action1)
    }

    @Test
    fun `create flow from kaskade no emissions on initialized`() {
        val stateFlow = kaskade.stateFlow()
        stateFlow.subscribe {
            throw AssertionError("Should not emit anything")
        }
    }
}