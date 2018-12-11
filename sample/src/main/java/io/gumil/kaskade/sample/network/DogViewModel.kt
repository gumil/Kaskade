package io.gumil.kaskade.sample.network

import androidx.lifecycle.ViewModel
import io.gumil.kaskade.*
import io.gumil.kaskade.livedata.stateLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

internal class DogViewModel(
        private val dogApi: RandomDogApi
): ViewModel() {

    constructor(): this(ApiFactory.create())

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

        on<DogAction.OnError> { DogState.Error(action.exception) }
    }

    val state = kaskade.stateLiveData(DogAction.GetDog)

    fun process(action: DogAction) {
        kaskade.process(action)
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
        kaskade.unsubscribe()
    }
}

internal sealed class DogState : State {
    object Loading : DogState()
    data class Error(val exception: Throwable) : DogState()
    data class OnLoaded(val url: String) : DogState()
}

internal sealed class DogAction : Action {
    object Refresh : DogAction()
    object GetDog : DogAction()
    data class OnError(val exception: Throwable) : DogAction()
}