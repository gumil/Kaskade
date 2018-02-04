package io.gumil.kaskade

abstract class Deferred<T>(
        var onNext: (T) -> Unit = {},
        var onError: (Throwable) -> Unit = {}
) : Function0<Unit>

class DeferredValue<T>(
        private val value: T,
        onNext: (T) -> Unit = {},
        onError: (Throwable) -> Unit = {}
) : Deferred<T>(onNext, onError) {
    override fun invoke() {
        onNext(value)
    }
}