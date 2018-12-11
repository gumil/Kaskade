package io.gumil.kaskade.sample.network

import io.gumil.kaskade.Action
import io.gumil.kaskade.Kaskade
import io.gumil.kaskade.State
import io.gumil.kaskade.stateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

internal class DogKaskade(
        private val dogApi: RandomDogApi
) {

    private val job = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private val kaskade = Kaskade.create<DogState, DogAction>(DogState.OnLoaded("")) {
        on<DogAction.Refresh> {
            process(DogAction.GetDog)
            DogState.Loading
        }

        on<DogAction.GetDog>(uiScope) {
            DogState.OnLoaded(dogApi.getDog().await().message)
        }
    }

    val state = kaskade.stateFlow()

    fun process(action: DogAction) {
        kaskade.process(action)
    }

    fun unsubscribe() {
        job.cancel()
        state.unsubscribe()
        kaskade.unsubscribe()
    }
}

internal sealed class DogState : State {
    object Loading : DogState()
    data class OnLoaded(val url: String) : DogState()
}

internal sealed class DogAction : Action {
    object Refresh : DogAction()
    object GetDog : DogAction()
}