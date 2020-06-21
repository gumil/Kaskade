package dev.gumil.kaskade.sample.network

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dev.gumil.kaskade.coroutines.coroutines
import dev.gumil.kaskade.Action
import dev.gumil.kaskade.Kaskade
import dev.gumil.kaskade.SingleEvent
import dev.gumil.kaskade.State
import dev.gumil.kaskade.livedata.stateDamLiveData
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

internal class DogViewModel(
    private val dogApi: RandomDogApi,
    scope: CoroutineScope? = null
) : ViewModel() {

    constructor() : this(ApiFactory.create())

    private val job = scope?.coroutineContext?.get(Job) ?: Job()

    private val uiScope = scope ?: CoroutineScope(Dispatchers.Main + job)

    @field:Suppress("LateinitUsage")
    private lateinit var kaskade: Kaskade<DogAction, DogState>

    val state: LiveData<DogState> get() = _state

    private val _state by lazy {
        kaskade.stateDamLiveData()
    }

    fun restore(state: DogState = DogState.Loading) {
        if (::kaskade.isInitialized.not()) {
            kaskade = Kaskade.create(state) {
                on<DogAction.Refresh> {
                    process(DogAction.GetDog)
                    DogState.Loading
                }

                coroutines(uiScope) {
                    on<DogAction.GetDog> {
                        val dog = withContext(Dispatchers.IO) {
                            dogApi.getDog().message
                        }
                        DogState.OnLoaded(dog)
                    }
                }

                on<DogAction.OnError> { DogState.Error(action.exception) }
            }
            if (state is DogState.Loading) {
                process(DogAction.GetDog)
            }
        }
    }

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

sealed class DogState : State, Parcelable {
    @Parcelize object Loading : DogState(), SingleEvent
    @Parcelize data class Error(val exception: Throwable) : DogState(), SingleEvent
    @Parcelize data class OnLoaded(val url: String) : DogState()
}

sealed class DogAction : Action {
    object Refresh : DogAction()
    object GetDog : DogAction()
    data class OnError(val exception: Throwable) : DogAction()
}
