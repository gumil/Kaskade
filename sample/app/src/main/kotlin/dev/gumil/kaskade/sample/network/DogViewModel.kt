package dev.gumil.kaskade.sample.network

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.gumil.kaskade.coroutines.coroutines
import dev.gumil.kaskade.Action
import dev.gumil.kaskade.Kaskade
import dev.gumil.kaskade.SingleEvent
import dev.gumil.kaskade.State
import dev.gumil.kaskade.livedata.stateDamLiveData
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val KEY_STATE = "last_state"

internal class DogViewModel(
    private val dogApi: RandomDogApi,
    private val savedStateHandle: SavedStateHandle,
    scope: CoroutineScope? = null
) : ViewModel() {

    /**
     * This is internally used by the SavedStateViewModelFactory
     */
    @Suppress("unused")
    constructor(savedStateHandle: SavedStateHandle) : this(ApiFactory.create(), savedStateHandle)

    private val uiScope = scope ?: viewModelScope

    private val kaskade: Kaskade<DogAction, DogState>

    init {
        val initialState = savedStateHandle.get<DogState>(KEY_STATE) ?: DogState.Loading
        kaskade = Kaskade.create(initialState) {
            on<DogAction.Refresh> {
                dispatch(DogAction.GetDog)
                DogState.Loading
            }

            coroutines(uiScope) {
                on<DogAction.GetDog> {
                    val dog = withContext(Dispatchers.IO) {
                        dogApi.getDog().message
                    }
                    val state = DogState.OnLoaded(dog)
                    savedStateHandle[KEY_STATE] = state
                    state
                }
            }

            on<DogAction.OnError> { DogState.Error(action.exception) }
        }
        if (initialState is DogState.Loading) {
            dispatch(DogAction.GetDog)
        }
    }

    val state: LiveData<DogState> get() = _state

    private val _state by lazy {
        kaskade.stateDamLiveData()
    }

    fun dispatch(action: DogAction) {
        kaskade.dispatch(action)
    }

    override fun onCleared() {
        super.onCleared()
        _state.clear()
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
