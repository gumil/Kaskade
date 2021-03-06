package dev.gumil.kaskade.sample.network

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import io.mockk.coEvery
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

internal class DogViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val mockUrl = "MockDog"

    private val mockDog = Dog(mockUrl)

    private val mockApi = mockk<RandomDogApi>()

    private val savedStateHandle = SavedStateHandle()

    @Before
    fun setUp() {
        coEvery { mockApi.getDog() } returns mockDog
    }

    @Test
    fun `dispatch Refresh should emit Loading and OnLoaded states`() {
        val mockObserver = mockk<Observer<DogState>>(relaxed = true)
        runBlocking {
            val viewModel = DogViewModel(mockApi, savedStateHandle, this)
            viewModel.state.observeForever(mockObserver)
            viewModel.dispatch(DogAction.Refresh)
        }
        verify(exactly = 2) { mockObserver.onChanged(DogState.Loading) }
        verify(exactly = 2) { mockObserver.onChanged(DogState.OnLoaded(mockUrl)) }
        confirmVerified(mockObserver)
    }

    @Test
    fun `dispatch OnError action should emit Error state`() {
        val mockObserver = mockk<Observer<DogState>>(relaxed = true)
        val exception = Exception()
        runBlocking {
            val viewModel = DogViewModel(mockApi, savedStateHandle, this)
            viewModel.state.observeForever(mockObserver)
            viewModel.dispatch(DogAction.OnError(exception))
        }
        verify(exactly = 1) { mockObserver.onChanged(DogState.Loading) }
        verify(exactly = 1) { mockObserver.onChanged(DogState.OnLoaded(mockUrl)) }
        verify(exactly = 1) { mockObserver.onChanged(DogState.Error(exception)) }
        confirmVerified(mockObserver)
    }

    @Test
    fun `dispatch GetDog action should emit OnLoaded state`() {
        val mockObserver = mockk<Observer<DogState>>(relaxed = true)
        runBlocking {
            val viewModel = DogViewModel(mockApi, savedStateHandle, this)
            viewModel.state.observeForever(mockObserver)
            viewModel.dispatch(DogAction.GetDog)
        }
        verify(exactly = 1) { mockObserver.onChanged(DogState.Loading) }
        verify(exactly = 2) { mockObserver.onChanged(DogState.OnLoaded(mockUrl)) }
        confirmVerified(mockObserver)
    }

    @Test
    fun `restore with state should only emit passed state`() {
        val mockObserver = mockk<Observer<DogState>>(relaxed = true)
        val state = DogState.OnLoaded(mockUrl)
        savedStateHandle["last_state"] = state
        runBlocking {
            val viewModel = DogViewModel(mockApi, savedStateHandle, this)
            viewModel.state.observeForever(mockObserver)
        }
        verify(exactly = 0) { mockObserver.onChanged(DogState.Loading) }
        verify(exactly = 1) { mockObserver.onChanged(state) }
        confirmVerified(mockObserver)
    }
}
