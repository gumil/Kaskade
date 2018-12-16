package io.gumil.kaskade.sample.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.gumil.kaskade.coroutines.coroutines
import io.gumil.kaskade.Action
import io.gumil.kaskade.Kaskade
import io.gumil.kaskade.State
import io.gumil.kaskade.livedata.stateDamLiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

internal class DogViewModel(
        private val dogApi: RandomDogApi,
        dispatcher: CoroutineDispatcher = Dispatchers.Main
): ViewModel() {

    constructor(): this(ApiFactory.create())

    private val job = Job()

    private val uiScope = CoroutineScope(dispatcher + job)

    private val kaskade = Kaskade.create<DogAction, DogState>(DogState.OnLoaded("")) {
        on<DogAction.Refresh> {
            process(DogAction.GetDog)
            DogState.Loading
        }

        coroutines(uiScope) {
            on<DogAction.GetDog> {
                DogState.OnLoaded(dogApi.getDog().await().message)
            }
        }

        on<DogAction.OnError> { DogState.Error(action.exception) }
    }

    val state: LiveData<DogState> get() = _state

    private val _state = kaskade.stateDamLiveData(DogAction.GetDog,
            DogState.Error::class, DogState.Loading::class)

    fun process(action: DogAction) {
        kaskade.process(action)
    }

    override fun onCleared() {
        super.onCleared()
        _state.clear()
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