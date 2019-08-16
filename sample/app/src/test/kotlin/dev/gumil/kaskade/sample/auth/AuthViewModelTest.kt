package dev.gumil.kaskade.sample.auth

import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.observers.TestObserver
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class AuthViewModelTest {

    @Before
    fun setUp() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler {
            Schedulers.trampoline()
        }
        RxJavaPlugins.setIoSchedulerHandler {
            Schedulers.trampoline()
        }
        RxJavaPlugins.setComputationSchedulerHandler {
            Schedulers.trampoline()
        }
    }

    @Test
    fun `process login should emit Loading and Success states`() {
        val viewModel = AuthViewModel(0)
        val observer = TestObserver<AuthState>()
        viewModel.state.subscribe(observer)

        viewModel.process(Observable.just(AuthAction.Login("hello", "world")))

        observer.assertValues(AuthState.Loading, AuthState.Success)
        observer.assertNoErrors()
    }

    @Test
    fun `process login with error should emit Loading and Error states`() {
        val viewModel = AuthViewModel(0)
        val observer = TestObserver<AuthState>()
        viewModel.state.subscribe(observer)

        viewModel.process(Observable.just(AuthAction.Login("hello", "error")))

        println(observer.values())
        observer.assertValues(AuthState.Loading, AuthState.Error)
        observer.assertNoErrors()
    }

    @Test
    fun `process onError should emit error state`() {
        val viewModel = AuthViewModel(0)
        val observer = TestObserver<AuthState>()
        viewModel.state.subscribe(observer)

        viewModel.process(Observable.just(AuthAction.OnError))

        observer.assertValues(AuthState.Error)
        observer.assertNoErrors()
    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }
}
