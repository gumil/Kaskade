/*
 * The MIT License (MIT)
 *
 * Copyright 2017 Miguel Panelo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.gumil.kaskade

import kotlin.properties.Delegates

class StateMachine<S : State, I : Intent, R : Result<S>>(
        initialState: S
) {
    private val intentResultMap = mutableMapOf<Intent, Deferred<R>>()

    private var currentState: S by Delegates.observable(initialState) { _, _, newValue ->
        onStateChanged?.invoke(newValue)
    }

    var onStateChanged: ((state: S) -> Unit)? = null

    private val deferredList = mutableListOf<Deferred<*>>()

    fun addIntentHandler(intent: Intent, deferred: Deferred<R>) {
        deferredList.add(deferred)
        intentResultMap[intent] = deferred
    }

    fun processIntent(intent: I) {
        intentResultMap[intent]?.apply {
            _onNext = {
                currentState = it.reduceToState(currentState)
            }
        }?.invoke() ?: throw IllegalStateException("Intent ${intent.javaClass.simpleName} not added to State Machine")
    }

    fun processIntent(intent: Deferred<I>) {
        deferredList.add(intent)
        intent._onNext = { processIntent(it) }
    }

    fun dispose() {
        deferredList.forEach { it.dispose() }
    }
}