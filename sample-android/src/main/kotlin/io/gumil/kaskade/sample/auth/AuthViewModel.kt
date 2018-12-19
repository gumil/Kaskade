package io.gumil.kaskade.sample.auth

import androidx.lifecycle.ViewModel
import io.gumil.kaskade.Action
import io.gumil.kaskade.Kaskade
import io.gumil.kaskade.State
import io.gumil.kaskade.rx.rx
import io.gumil.kaskade.rx.stateObservable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

internal class AuthViewModel(
        private val delay: Long = 5
) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val observer get() = object : DisposableObserver<AuthState>() {
        override fun onComplete() {
            //do nothing
        }

        override fun onNext(state: AuthState) {
            /**
             * Do sideffects here like logging. Sending state already handled by Kaskade
             */
        }

        override fun onError(e: Throwable) {
            process(Observable.just(AuthAction.OnError))
        }

    }.also { disposables.add(it) }

    private val kaskade = Kaskade.create<AuthAction, AuthState>(AuthState.Initial) {
        rx {
            on<AuthAction.Login>({ observer }) {
                delay(delay, TimeUnit.SECONDS)
                        .map {
                            if (it.action.password == "world" &&
                                    it.action.username == "hello") {
                                AuthState.Success
                            } else {
                                AuthState.Error
                            }
                        }
                        .ofType(AuthState::class.java)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .startWith(AuthState.Loading)
            }
        }

        on<AuthAction.OnError> {
            AuthState.Error
        }
    }

    val state: Observable<AuthState> = kaskade.stateObservable()

    fun process(actions: Observable<AuthAction>) {
        actions.subscribe { kaskade.process(it) }.also { disposables.add(it) }
    }

    override fun onCleared() {
        super.onCleared()
        kaskade.unsubscribe()
        disposables.clear()
    }

    companion object {
        private const val TAG = "AuthViewModel"
    }
}

internal sealed class AuthState : State {
    object Initial : AuthState()
    object Loading : AuthState()
    object Error : AuthState()
    object Success : AuthState()
}

internal sealed class AuthAction : Action {
    data class Login(val username: String, val password: String) : AuthAction()
    object OnError : AuthAction()
}