package io.gumil.kaskade

abstract class Deferred<T>(
        val onError: (Throwable) -> Unit = {}
) : Function0<Unit> {

    val onNext: (T) -> Unit get() =  _onNext
    internal var _onNext: (T) -> Unit = {}

    abstract fun dispose()
}

class DeferredValue<T>(
        private val value: T,
        onError: (Throwable) -> Unit = {}
) : Deferred<T>(onError) {

    override fun dispose() {}

    override fun invoke() {
        try {
            onNext(value)
        } catch (e: Exception) {
            onError(e)
        }
    }
}