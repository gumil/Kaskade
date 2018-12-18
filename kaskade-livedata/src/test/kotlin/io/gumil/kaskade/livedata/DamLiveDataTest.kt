package io.gumil.kaskade.livedata

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.gumil.kaskade.*
import org.junit.Rule
import org.junit.rules.TestRule
import java.lang.AssertionError
import kotlin.test.*

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
    }

    @BeforeTest
    fun `should emit initial state`() {
        kaskade.stateDamLiveData().observeForever {
            assertEquals(TestState.State1, it)
        }
    }

    @Test
    fun `damLiveData when value sent should invoke observer`() {
        val liveData = DamLiveData<String>()
        liveData.observeForever {
            assertEquals("hello", it)
        }
        liveData.setValue("hello")
    }

    @Test
    fun `damLiveData invoke latest emitted value before observing`() {
        val liveData = DamLiveData<String>()
        var counter = 0

        liveData.setValue("test")
        liveData.setValue("world")
        liveData.observeForever {
            if (counter++ == 0) {
                assertEquals("world", it)
                return@observeForever
            }
            assertEquals("hello", it)
        }
        liveData.setValue("hello")
    }

    @Test
    fun `damLiveData should not invoke anything after removing observer`() {
        val liveData = DamLiveData<String>()

        val observer = Observer<String>  {
            throw AssertionError("Should not emit anything")
        }

        liveData.observeForever(observer)
        liveData.removeObserver(observer)
        liveData.setValue( "hello")
    }

    @Test
    fun `damLiveData should invoke last emitted after removeObserver`() {
        val liveData = DamLiveData<String>()

        val observer = Observer<String>  {
            throw AssertionError("Should not emit anything")
        }

        liveData.observeForever(observer)
        liveData.removeObserver(observer)
        liveData.setValue("hello")
        liveData.observeForever {
            assertEquals("hello", it)
        }
    }

    @Test
    fun `damLiveData should not invoke last emitted after cleared`() {
        val liveData = DamLiveData<String>()

        val observer = Observer<String> {
            assertEquals("hello", it)
        }

        liveData.observeForever(observer)
        liveData.setValue("hello")
        liveData.clear()
        liveData.removeObserver(observer)
        liveData.setValue("world")
    }

    @Test
    fun `create DamLivedata from kaskade using extension function`() {
        val stateLiveData = kaskade.stateDamLiveData()

        stateLiveData.observeForever {
            assertEquals(TestState.State1, it)
        }

        kaskade.process(TestAction.Action1)
    }

    @Test
    fun `create DamLiveData from kaskade no emissions on initialized`() {
        val stateLiveData = kaskade.stateDamLiveData()
        stateLiveData.observeForever {
            throw AssertionError("Should not emit anything")
        }
    }

    @Test
    fun `create DamLiveData from kaskade with initial action`() {
        val stateLiveData = kaskade.stateDamLiveData(TestAction.Action1)

        stateLiveData.observeForever {
            assertEquals(TestState.State1, it)
        }
    }

    @Test
    fun `create DamLiveData from kaskade should not emit excluded state on new observer`() {
        val stateLiveData = kaskade.stateDamLiveData(TestAction.Action1, TestState.State2::class)
        var counter = 0

        val observer = Observer<TestState> {
            if (counter++ == 1) {
                assertEquals(TestState.State2, it)
            } else {
                assertEquals(TestState.State1, it)
            }
        }
        stateLiveData.observeForever(observer)

        kaskade.process(TestAction.Action2)

        stateLiveData.removeObserver(observer)

        stateLiveData.observeForever(observer)
    }
}