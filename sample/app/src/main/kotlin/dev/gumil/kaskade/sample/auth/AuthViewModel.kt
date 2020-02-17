package dev.gumil.kaskade.sample.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import dev.gumil.kaskade.Action
import dev.gumil.kaskade.Kaskade
import dev.gumil.kaskade.State
import dev.gumil.kaskade.rx.rx
import dev.gumil.kaskade.rx.stateObservable
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

    private val observer
        get() = object : DisposableObserver<AuthState>() {
            override fun onComplete() {
                // do nothing
            }

            override fun onNext(state: AuthState) {
                /**
                 * Do sideffects here like logging. Sending state already handled by Kaskade
                 */
                Log.d("AuthViewModel", "state on next = $state")
            }

            override fun onError(e: Throwable) {
                process(Observable.just(AuthAction.OnError))
            }
        }.also { disposables.add(it) }

    private val kaskade = Kaskade.create<AuthAction, AuthState>(AuthState.Initial) {
        rx {
            on<AuthAction.Login>({ observer }) {
                delay(delay, TimeUnit.SECONDS)
                    .map { actionState ->
                        if (actionState.action.password == "world" &&
                            actionState.action.username == "hello"
                        ) {
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
        actions.subscribe { kaskade.dispatch(it) }.also { disposables.add(it) }
    }

    override fun onCleared() {
        super.onCleared()
        kaskade.unsubscribe()
        disposables.clear()
    }
}

sealed class AuthState : State {
    object Initial : AuthState()
    object Loading : AuthState()
    object Error : AuthState()
    object Success : AuthState()
}

sealed class AuthAction : Action {
    data class Login(val username: String, val password: String) : AuthAction()
    object OnError : AuthAction()
}
