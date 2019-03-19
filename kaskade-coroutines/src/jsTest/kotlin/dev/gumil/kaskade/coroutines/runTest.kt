package dev.gumil.kaskade.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.promise
import kotlinx.coroutines.GlobalScope

actual fun runTest(block: suspend CoroutineScope.() -> Unit, then: () -> Unit): dynamic =
    GlobalScope.promise { block(this) }.then { then() }
