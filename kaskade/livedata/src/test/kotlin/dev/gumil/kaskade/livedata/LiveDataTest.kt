package dev.gumil.kaskade.livedata

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
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

internal class LiveDataTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

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
        val mockObserver = mockk<Observer<TestState>>(relaxed = true)
        kaskade.stateLiveData().observeForever(mockObserver)
        verify { mockObserver.onChanged(TestState.State1) }
    }

    @Test
    fun `livedata no emissions on initialized`() {
        val liveData = kaskade.stateLiveData()
        val mockObserver = mockk<Observer<TestState>>(relaxed = true)

        liveData.observeForever(mockObserver)

        verify(exactly = 0) { mockObserver.onChanged(any()) }
        confirmVerified(mockObserver)
    }

    @Test
    fun `set value on livedata should invoke observer`() {
        val liveData = kaskade.stateLiveData() as MutableLiveData<TestState>
        val mockObserver = mockk<Observer<TestState>>(relaxed = true)

        liveData.observeForever(mockObserver)
        liveData.value = TestState.State1

        verify { mockObserver.onChanged(TestState.State1) }
        confirmVerified(mockObserver)
    }

    @Test
    fun `livedata invoke latest emitted value before observing`() {
        val liveData = kaskade.stateLiveData() as MutableLiveData<TestState>
        val mockObserver = mockk<Observer<TestState>>(relaxed = true)

        liveData.value = TestState.State1
        liveData.value = TestState.State2
        liveData.value = TestState.State1
        liveData.observeForever(mockObserver)
        liveData.value = TestState.State2

        verifyOrder {
            mockObserver.onChanged(TestState.State1)
            mockObserver.onChanged(TestState.State2)
        }
        confirmVerified(mockObserver)
    }

    @Test
    fun `livedata should not invoke anything after removing observer`() {
        val liveData = kaskade.stateLiveData() as MutableLiveData<TestState>
        val mockObserver = mockk<Observer<TestState>>(relaxed = true)

        liveData.observeForever(mockObserver)
        liveData.removeObserver(mockObserver)
        liveData.value = TestState.State1

        verify(exactly = 0) { mockObserver.onChanged(TestState.State1) }
        verify { mockObserver.equals(mockObserver) }
        confirmVerified(mockObserver)
    }

    @Test
    fun `livedata should invoke last emitted after removeObserver`() {
        val liveData = kaskade.stateLiveData() as MutableLiveData<TestState>
        val mockObserver = mockk<Observer<TestState>>(relaxed = true)

        liveData.observeForever(mockObserver)
        liveData.removeObserver(mockObserver)
        liveData.value = TestState.State1
        liveData.observeForever(mockObserver)

        verify { mockObserver.onChanged(TestState.State1) }
        verify { mockObserver.equals(mockObserver) }
        confirmVerified(mockObserver)
    }

    @Test
    fun `livedata should only emit dispatched state`() {
        val mockObserver = mockk<Observer<TestState>>(relaxed = true)
        val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
            on<TestAction.Action1> {
                TestState.State1
            }
            on<TestAction.Action2> {
                TestState.State2
            }
        }

        kaskade.dispatch(TestAction.Action2)

        kaskade.stateLiveData().observeForever(mockObserver)

        verify(exactly = 0) { mockObserver.onChanged(TestState.State1) }
        verify { mockObserver.onChanged(TestState.State2) }
        confirmVerified(mockObserver)
    }
}
