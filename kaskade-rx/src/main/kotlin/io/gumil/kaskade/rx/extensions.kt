/*
 * Copyright 2018 Miguel Panelo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.gumil.kaskade.rx

import io.gumil.kaskade.Deferred
import io.gumil.kaskade.Action
import io.gumil.kaskade.Result
import io.gumil.kaskade.State
import io.gumil.kaskade.StateMachine
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

fun <T> Observable<T>.toDeferred(
        onError: (Throwable) -> Unit = {},
        onComplete: () -> Unit = {}
): Deferred<T> = RxDeferredValue(this, onError, onComplete)

class RxDeferredValue<T>(
        private val function: Observable<T>,
        onError: (Throwable) -> Unit = {},
        private val onComplete: () -> Unit = {}
) : Deferred<T>(onError) {

    private var subscription: Disposable? = null

    override fun invoke() {
        subscription = function.subscribe({
            onNext(it)
        }, {
            onError(it)
        }, {
            onComplete()
        })
    }

    override fun dispose() {
        subscription?.dispose()
    }
}

fun <S : State, A : Action, R : Result<S>> StateMachine<S, A, R>.stateObservable(): Observable<S> =
        PublishSubject.create<S>().apply {
            onStateChanged = {
                onNext(it)
            }
        }