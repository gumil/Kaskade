package io.gumil.kaskade.livedata

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.gumil.kaskade.Kaskade
import org.junit.Rule
import org.junit.rules.TestRule
import kotlin.test.Test
import kotlin.test.assertEquals

internal class LiveDataTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val kaskade = Kaskade.create<TestState, TestAction>(TestState.State1) {
        on<TestAction.Action1> {
            TestState.State1
        }
        on<TestAction.Action2> {
            TestState.State2
        }
    }

    private val liveData = kaskade.stateLiveData() as MutableLiveData<TestState>


    @Test
    fun `livedata no emissions on initialized`() {
        liveData.observeForever {
            throw AssertionError("Should not emit anything")
        }
    }

    @Test
    fun `create livedata with initial action`() {
        val livedata = kaskade.stateLiveData(TestAction.Action1)

        livedata.observeForever {
            assertEquals(TestState.State1, it)
        }
    }

    @Test
    fun `set value on livedata should invoke observer`() {
        liveData.observeForever {
            assertEquals(TestState.State1, it)
        }
        liveData.value = TestState.State1
    }

    @Test
    fun `livedata invoke latest emitted value before observing`() {
        liveData.value = TestState.State1
        var counter = 0

        liveData.value = TestState.State2
        liveData.value = TestState.State1
        liveData.observeForever {
            if (counter++ == 0) {
                assertEquals(TestState.State1, it)
                return@observeForever
            }
            assertEquals(TestState.State2, it)
        }
        liveData.value = TestState.State2
    }

    @Test
    fun `livedata should not invoke anything after removing observer`() {
        val observer = Observer<TestState>  {
            throw AssertionError("Should not emit anything")
        }

        liveData.observeForever(observer)
        liveData.removeObserver(observer)
        liveData.value = TestState.State1
    }

    @Test
    fun `livedata should invoke last emitted after removeObserver`() {
        val observer = Observer<TestState>  {
            throw AssertionError("Should not emit anything")
        }

        liveData.observeForever(observer)
        liveData.removeObserver(observer)
        liveData.value = TestState.State1
        liveData.observeForever {
            assertEquals(TestState.State1, it)
        }
    }
}