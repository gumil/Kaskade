package io.gumil.kaskade.rx

import io.gumil.kaskade.Action
import io.gumil.kaskade.Kaskade
import io.gumil.kaskade.State
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import kotlin.test.*

internal class RxReducerTest {

    private val observer = TestObserver<TestState>()

    private val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
        rx {
            on<TestAction.Action1>({ observer }) {
                Observable.just(TestState.State1)
            }
        }

        on<TestAction.Action2> { TestState.State2 }
    }

    @Test
    fun `create rx kaskade builder`() {
        kaskade.process(TestAction.Action1)

        observer.assertValue(TestState.State1)
        observer.assertResult(TestState.State1)
        observer.assertNoErrors()
        observer.assertComplete()
    }

    @Test
    fun `observable not called`() {
        kaskade.process(TestAction.Action2)
        observer.assertNotSubscribed()
        observer.assertNotComplete()
    }

    @Test
    fun `observable has start value`() {
        val observer = TestObserver<TestState>()

        val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
            rx {
                on<TestAction.Action1>({ observer }) {
                    Observable.just<TestState>(TestState.State1)
                            .startWith(TestState.State2)
                }
            }
        }

        kaskade.process(TestAction.Action1)

        observer.assertValues(TestState.State2, TestState.State1)
        observer.assertResult(TestState.State2, TestState.State1)
        observer.assertNoErrors()
        observer.assertComplete()
    }

    @Test
    fun `observable has error`() {
        val observer = TestObserver<TestState>()
        val exception = Exception()

        val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
            rx {
                on<TestAction.Action1>({ observer }) {
                    Observable.error(exception)
                }
            }
        }

        kaskade.process(TestAction.Action1)

        observer.assertError(exception)
        observer.assertNotComplete()
    }

    @Test
    @Ignore
    fun `resubscribing observable`() {

    }

    private sealed class TestState : State {
        object State1 : TestState()
        object State2 : TestState()
    }

    private sealed class TestAction : Action {
        object Action1 : TestAction()
        object Action2 : TestAction()
    }
}