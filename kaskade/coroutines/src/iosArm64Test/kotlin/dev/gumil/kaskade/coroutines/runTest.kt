package dev.gumil.kaskade.coroutines

import kotlinx.coroutines.CoroutineScope

@Suppress("UnusedPrivateMember")
actual fun runTest(block: suspend CoroutineScope.() -> Unit, then: () -> Unit) {
    /**
     * This method is only implemented here to satisfy the compiler
     */
    UnsupportedOperationException("Should not be used")
}
