package io.gumil.kaskade.sample.network

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test

internal class DogViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val mockUrl = "MockDog"

    private val mockDog = Dog(mockUrl)

    private val mockDeferredDog = mockk<Deferred<Dog>>().apply {
        coEvery { await() } returns mockDog
    }

    private val mockApi = mockk<RandomDogApi>().apply {
        every { getDog() } returns mockDeferredDog
    }

    @Test
    fun `process Refresh should emit Loading and OnLoaded states`() {
        val mockObserver = mockk<Observer<DogState>>(relaxed = true)
        runBlocking {
            val viewModel = DogViewModel(mockApi, this)
            viewModel.restore()
            viewModel.state.observeForever(mockObserver)
            viewModel.process(DogAction.Refresh)
        }
        verify(exactly = 2) { mockObserver.onChanged(DogState.Loading) }
        verify(exactly = 2) { mockObserver.onChanged(DogState.OnLoaded(mockUrl)) }
    }

    @Test
    fun `process OnError action should emit Error state`() {
        val mockObserver = mockk<Observer<DogState>>(relaxed = true)
        val exception = Exception()
        runBlocking {
            val viewModel = DogViewModel(mockApi, this)
            viewModel.restore()
            viewModel.state.observeForever(mockObserver)
            viewModel.process(DogAction.OnError(exception))
        }
        verify(exactly = 1) { mockObserver.onChanged(DogState.Loading) }
        verify(exactly = 1) { mockObserver.onChanged(DogState.OnLoaded(mockUrl)) }
        verify(exactly = 1) { mockObserver.onChanged(DogState.Error(exception)) }
    }

    @Test
    fun `process GetDog action should emit OnLoaded state`() {
        val mockObserver = mockk<Observer<DogState>>(relaxed = true)
        runBlocking {
            val viewModel = DogViewModel(mockApi, this)
            viewModel.restore()
            viewModel.state.observeForever(mockObserver)
            viewModel.process(DogAction.GetDog)
        }
        verify(exactly = 1) { mockObserver.onChanged(DogState.Loading) }
        verify(exactly = 2) { mockObserver.onChanged(DogState.OnLoaded(mockUrl)) }
    }

    @Test
    fun `restore with state should only emit passed state`() {
        val mockObserver = mockk<Observer<DogState>>(relaxed = true)
        val state = DogState.OnLoaded(mockUrl)
        runBlocking {
            val viewModel = DogViewModel(mockApi, this)
            viewModel.restore(state)
            viewModel.state.observeForever(mockObserver)
        }
        verify(exactly = 0) { mockObserver.onChanged(DogState.Loading) }
        verify(exactly = 1) { mockObserver.onChanged(state) }
    }
}