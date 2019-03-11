package io.gumil.kaskade.flow

import io.gumil.kaskade.Kaskade
import io.gumil.kaskade.TestAction
import io.gumil.kaskade.TestState
import io.gumil.kaskade.stateDamFlow
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
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
    fun `damFlow when value sent should invoke subscribe`() {
        val flow = DamFlow<String>()
        val mockSubscriber = mockk<(String) -> Unit>()
        every { mockSubscriber.invoke(any()) } returns Unit

        flow.subscribe(mockSubscriber)
        flow.sendValue("hello")

        verify { mockSubscriber.invoke("hello") }
        confirmVerified(mockSubscriber)
    }

    @Test
    fun `damFlow invoke latest emitted value before subscribe`() {
        val flow = DamFlow<String>()
        val mockSubscriber = mockk<(String) -> Unit>()
        every { mockSubscriber.invoke(any()) } returns Unit

        flow.sendValue("test")
        flow.sendValue("world")
        flow.subscribe(mockSubscriber)
        flow.sendValue("hello")

        verifyOrder {
            mockSubscriber.invoke("world")
            mockSubscriber.invoke("hello")
        }
        confirmVerified(mockSubscriber)
    }

    @Test
    fun `damFlow should not invoke anything after unsubscribe`() {
        val flow = DamFlow<String>()
        val mockSubscriber = mockk<(String) -> Unit>()

        flow.subscribe(mockSubscriber)
        flow.unsubscribe()
        flow.sendValue("hello")

        verify(exactly = 0) { mockSubscriber.invoke("hello") }
        confirmVerified(mockSubscriber)
    }

    @Test
    fun `damFlow should invoke last emitted after unsubscribe`() {
        val flow = DamFlow<String>()
        val mockSubscriberNoEmission = mockk<(String) -> Unit>()
        val mockSubscriber = mockk<(String) -> Unit>()
        every { mockSubscriber.invoke(any()) } returns Unit

        flow.subscribe(mockSubscriberNoEmission)
        flow.unsubscribe()
        flow.sendValue("hello")
        flow.subscribe(mockSubscriber)

        verify(exactly = 0) { mockSubscriberNoEmission.invoke("hello") }
        verify { mockSubscriber.invoke("hello") }
        confirmVerified(mockSubscriber)
    }

    @Test
    fun `damFlow should not invoke last emitted after cleared`() {
        val flow = DamFlow<String>()
        val mockSubscriberNoEmission = mockk<(String) -> Unit>()
        val mockSubscriber = mockk<(String) -> Unit>()
        every { mockSubscriber.invoke(any()) } returns Unit

        flow.subscribe(mockSubscriber)
        flow.sendValue("hello")
        flow.clear()
        flow.subscribe(mockSubscriberNoEmission)

        verify(exactly = 0) { mockSubscriberNoEmission.invoke("hello") }
        verify { mockSubscriber.invoke("hello") }
        confirmVerified(mockSubscriber)
    }

    @Test
    fun `create flow from kaskade using extension function`() {
        val stateFlow = kaskade.stateDamFlow()
        val mockSubscriber = mockk<(TestState) -> Unit>()
        every { mockSubscriber.invoke(any()) } returns Unit

        stateFlow.subscribe(mockSubscriber)
        kaskade.process(TestAction.Action1)

        verify { mockSubscriber.invoke(TestState.State1) }
        confirmVerified(mockSubscriber)
    }

    @Test
    fun `create flow from kaskade no emissions on initialized`() {
        val stateFlow = kaskade.stateDamFlow()
        val mockSubscriber = mockk<(TestState) -> Unit>()
        stateFlow.subscribe(mockSubscriber)

        verify(exactly = 0) { mockSubscriber.invoke(any()) }
        confirmVerified(mockSubscriber)
    }

    @Test
    fun `create flow from kaskade should emit last state on new subscriber`() {
        val stateFlow = kaskade.stateDamFlow()
        val mockSubscriber = mockk<(TestState) -> Unit>()
        every { mockSubscriber.invoke(any()) } returns Unit

        kaskade.process(TestAction.Action1)
        stateFlow.unsubscribe()
        stateFlow.subscribe(mockSubscriber)

        verify { mockSubscriber.invoke(TestState.State1) }
        confirmVerified(mockSubscriber)
    }

    @Test
    fun `create flow from kaskade should not emit excluded state on new subscriber`() {
        val stateFlow = kaskade.stateDamFlow()
        val mockSubscriber = mockk<(TestState) -> Unit>()
        every { mockSubscriber.invoke(any()) } returns Unit

        kaskade.process(TestAction.Action1)
        stateFlow.subscribe(mockSubscriber)
        kaskade.process(TestAction.Action3)
        stateFlow.unsubscribe()
        stateFlow.subscribe(mockSubscriber)

        verifyOrder {
            mockSubscriber.invoke(TestState.State1)
            mockSubscriber.invoke(TestState.SingleStateEvent)
            mockSubscriber.invoke(TestState.State1)
        }
        confirmVerified(mockSubscriber)
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

        val mockSubscriber = mockk<(TestState) -> Unit>()
        every { mockSubscriber.invoke(any()) } returns Unit

        kaskade.process(TestAction.Action2)

        kaskade.stateDamFlow().subscribe(mockSubscriber)

        verifyOrder {
            mockSubscriber.invoke(TestState.State1)
            mockSubscriber.invoke(TestState.State2)
        }
        confirmVerified(mockSubscriber)
    }
}
