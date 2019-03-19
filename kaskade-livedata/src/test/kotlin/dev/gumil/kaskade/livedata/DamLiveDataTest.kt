package dev.gumil.kaskade.livedata

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import dev.gumil.kaskade.Kaskade
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.Rule
import org.junit.rules.TestRule
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class DamLiveDataTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

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

    private val mockObserver = mockk<Observer<TestState>>(relaxed = true)

    @BeforeTest
    fun `should emit initial state`() {
        kaskade.stateDamLiveData().observeForever(mockObserver)
        verify { mockObserver.onChanged(TestState.State1) }
    }

    @Test
    fun `damLiveData when value sent should invoke observer`() {
        val liveData = DamLiveData<String>()
        val mockObserver = mockk<Observer<String>>(relaxed = true)
        liveData.observeForever(mockObserver)
        val value = "hello"

        liveData.setValue(value)

        verify { mockObserver.onChanged(value) }
        confirmVerified(mockObserver)
    }

    @Test
    fun `damLiveData invoke latest emitted value before observing`() {
        val liveData = DamLiveData<String>()
        val mockObserver = mockk<Observer<String>>(relaxed = true)
        val hello = "hello"
        val world = "world"

        liveData.setValue("test")
        liveData.setValue(world)
        liveData.observeForever(mockObserver)
        liveData.setValue(hello)

        verifyOrder {
            mockObserver.onChanged(world)
            mockObserver.onChanged(hello)
        }
        confirmVerified(mockObserver)
    }

    @Test
    fun `damLiveData should not invoke anything after removing observer`() {
        val liveData = DamLiveData<String>()
        val mockObserver = mockk<Observer<String>>(relaxed = true)
        val value = "hello"

        liveData.observeForever(mockObserver)
        liveData.removeObserver(mockObserver)
        liveData.setValue(value)

        verify(exactly = 0) { mockObserver.onChanged(value) }
        verify { mockObserver.equals(mockObserver) }
        confirmVerified(mockObserver)
    }

    @Test
    fun `damLiveData should invoke last emitted after removeObserver`() {
        val liveData = DamLiveData<String>()
        val mockObserver = mockk<Observer<String>>(relaxed = true)
        val value = "hello"

        liveData.observeForever(mockObserver)
        liveData.removeObserver(mockObserver)
        liveData.setValue(value)
        liveData.observeForever(mockObserver)

        verify { mockObserver.onChanged(value) }
        verify { mockObserver.equals(mockObserver) }
        confirmVerified(mockObserver)
    }

    @Test
    fun `damLiveData should not invoke last emitted after cleared`() {
        val liveData = DamLiveData<String>()
        val mockObserver = mockk<Observer<String>>(relaxed = true)
        val hello = "hello"
        val world = "world"

        liveData.observeForever(mockObserver)
        liveData.setValue(hello)
        liveData.clear()
        liveData.removeObserver(mockObserver)
        liveData.setValue(world)

        verify { mockObserver.onChanged(hello) }
        verify { mockObserver.equals(mockObserver) }
        confirmVerified(mockObserver)
    }

    @Test
    fun `create DamLivedata from kaskade using extension function`() {
        kaskade.process(TestAction.Action1)

        verify { mockObserver.onChanged(TestState.State1) }
        confirmVerified(mockObserver)
    }

    @Test
    fun `create DamLiveData from kaskade no emissions on initialized`() {
        val stateLiveData = kaskade.stateDamLiveData()
        val mockObserver = mockk<Observer<TestState>>(relaxed = true)

        stateLiveData.observeForever(mockObserver)

        verify(exactly = 0) { mockObserver.onChanged(any()) }
        confirmVerified(mockObserver)
    }

    @Test
    fun `create DamLiveData from kaskade should not emit SingleEvent state on new observer`() {
        val stateLiveData = kaskade.stateDamLiveData()
        val mockObserver = mockk<Observer<TestState>>(relaxed = true)

        kaskade.process(TestAction.Action1)
        stateLiveData.observeForever(mockObserver)
        kaskade.process(TestAction.Action3)
        stateLiveData.removeObserver(mockObserver)
        stateLiveData.observeForever(mockObserver)

        verifyOrder {
            mockObserver.onChanged(TestState.State1)
            mockObserver.onChanged(TestState.SingleStateEvent)
            mockObserver.onChanged(TestState.State1)
        }
        verify { mockObserver.equals(mockObserver) }
        confirmVerified(mockObserver)
    }

    @Test
    fun `should emit initial state and processed state`() {
        val mockObserver = mockk<Observer<TestState>>(relaxed = true)
        val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
            on<TestAction.Action1> {
                TestState.State1
            }
            on<TestAction.Action2> {
                TestState.State2
            }
        }

        kaskade.process(TestAction.Action2)
        kaskade.stateDamLiveData().observeForever(mockObserver)

        verifyOrder {
            mockObserver.onChanged(TestState.State1)
            mockObserver.onChanged(TestState.State2)
        }
        confirmVerified(mockObserver)
    }
}
