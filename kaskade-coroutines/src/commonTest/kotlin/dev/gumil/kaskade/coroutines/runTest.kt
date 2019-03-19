package dev.gumil.kaskade.coroutines

import kotlinx.coroutines.CoroutineScope

@Suppress("UnusedPrivateMember") // Detekt sees the function parameters as unused
expect fun runTest(block: suspend CoroutineScope.() -> Unit, then: () -> Unit)
