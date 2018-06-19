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

package io.gumil.kaskade

abstract class Holder<T>(
        val onError: (Throwable) -> Unit = {}
) : Function0<Unit> {

    val onNext: (T) -> Unit get() =  _onNext
    internal var _onNext: (T) -> Unit = {}

    abstract fun dispose()
}

class HolderValue<T>(
        private val value: T,
        onError: (Throwable) -> Unit = {}
) : Holder<T>(onError) {

    private var isDisposed = false

    override fun dispose() {
        isDisposed = true
    }

    override fun invoke() {
        try {
            if (!isDisposed) {
                onNext(value)
            }
        } catch (e: Exception) {
            onError(e)
        }
    }
}