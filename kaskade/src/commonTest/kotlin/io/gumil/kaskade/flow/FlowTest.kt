package io.gumil.kaskade.flow

import io.gumil.kaskade.Kaskade
import io.gumil.kaskade.TestAction
import io.gumil.kaskade.TestState
import io.gumil.kaskade.stateFlow
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

internal class FlowTest {

    private val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
        on<TestAction.Action1> {
            TestState.State1
        }
    }

    private val mockStateChanged = mockk<(state: TestState) -> Unit>()

    init {
        every { mockStateChanged.invoke(any()) } returns Unit
        kaskade.onStateChanged = mockStateChanged
    }

    @BeforeTest
    fun `should emit initial state`() {
        verify { mockStateChanged.invoke(TestState.State1) }
    }

    @Test
    fun `mutableFlow when value sent should invoke subscribe`() {
        val flow = MutableFlow<String>()
        val mockSubscriber = mockk<(String) -> Unit>()
        every { mockSubscriber.invoke(any()) } returns Unit

        flow.subscribe(mockSubscriber)
        flow.sendValue("hello")

        verify { mockSubscriber.invoke("hello") }
        confirmVerified(mockSubscriber)
    }

    @Test
    fun `mutableFlow only invoke values after subscribe`() {
        val flow = MutableFlow<String>()
        val mockSubscriber = mockk<(String) -> Unit>()
        every { mockSubscriber.invoke(any()) } returns Unit

        flow.sendValue("world")
        flow.subscribe(mockSubscriber)
        flow.sendValue("hello")

        verify(exactly = 0) { mockSubscriber.invoke("world") }
        verify { mockSubscriber.invoke("hello") }
        confirmVerified(mockSubscriber)
    }

    @Test
    fun `mutableFlow should not invoke anything after unsubscribe`() {
        val flow = MutableFlow<String>()
        val mockSubscriber = mockk<(String) -> Unit>()

        flow.subscribe(mockSubscriber)
        flow.unsubscribe()
        flow.sendValue("hello")

        verify(exactly = 0) { mockSubscriber.invoke("hello") }
        confirmVerified(mockSubscriber)
    }

    @Test
    fun `create flow from kaskade using extension function`() {
        val stateFlow = kaskade.stateFlow()
        val mockSubscriber = mockk<(TestState) -> Unit>()
        every { mockSubscriber.invoke(any()) } returns Unit

        stateFlow.subscribe(mockSubscriber)
        kaskade.process(TestAction.Action1)

        assertTrue { stateFlow is MutableFlow<TestState> }
        verify { mockSubscriber.invoke(TestState.State1) }
        confirmVerified(mockSubscriber)
    }

    @Test
    fun `create flow from kaskade no emissions on initialized`() {
        val stateFlow = kaskade.stateFlow()
        val mockSubscriber = mockk<(TestState) -> Unit>()

        stateFlow.subscribe(mockSubscriber)

        verify(exactly = 0) { mockSubscriber.invoke(TestState.State1) }
        confirmVerified(mockSubscriber)
    }
}
