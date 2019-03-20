package dev.gumil.kaskade.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

actual fun runTest(block: suspend CoroutineScope.() -> Unit, then: () -> Unit) {
    runBlocking { block(this) }
    then()
}
