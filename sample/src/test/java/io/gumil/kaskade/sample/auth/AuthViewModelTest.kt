package io.gumil.kaskade.sample.auth

import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test

internal class AuthViewModelTest {

    private val viewModel = AuthViewModel(0)

    @Before
    fun setUp() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler {
            Schedulers.trampoline()
        }
    }

    @Test
    fun `process login should emit Loading and Success states`() {
        val observer = TestObserver<AuthState>()

        viewModel.process(Observable.just(AuthAction.Login("hello", "world")))

        viewModel.state.subscribe(observer)

        observer.assertValues(AuthState.Loading, AuthState.Success)
        observer.assertResult(AuthState.Loading, AuthState.Success)
        observer.assertNoErrors()
        observer.assertComplete()
    }

    @Test
    fun `process login with error should emit Loading and Error states`() {
        val observer = TestObserver<AuthState>()

        viewModel.process(Observable.just(AuthAction.Login("hello", "error")))

        viewModel.state.subscribe(observer)

        observer.assertValues(AuthState.Loading, AuthState.Error)
        observer.assertResult(AuthState.Loading, AuthState.Error)
        observer.assertNoErrors()
        observer.assertComplete()
    }

    @Test
    fun `process onError should emit error state`() {
        val observer = TestObserver<AuthState>()

        viewModel.process(Observable.just(AuthAction.Login("hello", "error")))

        viewModel.state.subscribe(observer)

        observer.assertValues(AuthState.Error)
        observer.assertResult(AuthState.Error)
        observer.assertNoErrors()
        observer.assertComplete()
    }
}
