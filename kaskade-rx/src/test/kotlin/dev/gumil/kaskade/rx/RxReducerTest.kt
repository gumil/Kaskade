package dev.gumil.kaskade.rx

import dev.gumil.kaskade.Action
import dev.gumil.kaskade.Kaskade
import dev.gumil.kaskade.State
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

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

    @BeforeTest
    fun `should emit initial state`() {
        kaskade.stateObservable().subscribe {
            assertEquals(TestState.State1, it)
        }
    }

    @Test
    fun `create rx kaskade builder`() {
        val testObserver = TestObserver<TestState>()
        kaskade.stateObservable().subscribe(testObserver)

        kaskade.process(TestAction.Action1)

        observer.assertValue(TestState.State1)
        observer.assertResult(TestState.State1)
        observer.assertNoErrors()
        observer.assertComplete()

        testObserver.assertValue(TestState.State1)
        testObserver.assertNoErrors()
        testObserver.assertNotComplete()
    }

    @Test
    fun `observable not called`() {
        val testObserver = TestObserver<TestState>()
        kaskade.stateObservable().subscribe(testObserver)

        kaskade.process(TestAction.Action2)

        observer.assertNotSubscribed()
        observer.assertNotComplete()
        observer.assertNoValues()

        testObserver.assertValue(TestState.State2)
        testObserver.assertNoErrors()
        testObserver.assertNotComplete()
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

        val testObserver = TestObserver<TestState>()
        kaskade.stateObservable().subscribe(testObserver)

        kaskade.process(TestAction.Action1)

        observer.assertValues(TestState.State2, TestState.State1)
        observer.assertResult(TestState.State2, TestState.State1)
        observer.assertNoErrors()
        observer.assertComplete()

        testObserver.assertValues(TestState.State2, TestState.State1)
        testObserver.assertNoErrors()
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

        val testObserver = TestObserver<TestState>()
        kaskade.stateObservable().subscribe(testObserver)

        kaskade.process(TestAction.Action1)

        observer.assertError(exception)
        observer.assertNotComplete()

        testObserver.assertNoValues()
        testObserver.assertNoErrors()
        testObserver.assertNotComplete()
    }

    @Test
    fun `send two actions`() {
        val kaskade = Kaskade.create<TestAction, TestState>(TestState.State1) {
            rx({ TestObserver<TestState>() }) {
                on<TestAction.Action1> {
                    Observable.just<TestState>(TestState.State1)
                }

                on<TestAction.Action2> {
                    Observable.just<TestState>(TestState.State2)
                }
            }
        }

        val testObserver = TestObserver<TestState>()
        kaskade.stateObservable().subscribe(testObserver)

        kaskade.process(TestAction.Action1)
        kaskade.process(TestAction.Action2)

        testObserver.assertValues(TestState.State1, TestState.State2)
        testObserver.assertNoErrors()
        testObserver.assertNotComplete()
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
