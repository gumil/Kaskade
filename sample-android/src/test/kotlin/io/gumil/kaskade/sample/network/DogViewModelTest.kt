package io.gumil.kaskade.sample.network

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import org.junit.Before
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

    private lateinit var viewModel: DogViewModel

    private val mockObserver = mockk<Observer<DogState>>(relaxed = true)

    @Before
    fun setUp() {
        viewModel = DogViewModel(mockApi, Dispatchers.Default)
        viewModel.restore()
        viewModel.state.observeForever(mockObserver)
        verify(exactly = 1) { mockObserver.onChanged(DogState.Loading) }
        verify(exactly = 1) { mockObserver.onChanged(DogState.OnLoaded(mockUrl)) }
    }

    @Test
    fun `process Refresh should emit Loading and OnLoaded states`() {
        viewModel.process(DogAction.Refresh)

        verify(exactly = 2) { mockObserver.onChanged(DogState.Loading) }
        verify(exactly = 2) { mockObserver.onChanged(DogState.OnLoaded(mockUrl)) }
    }

    @Test
    fun `process OnError action should emit Error state`() {
        val exception = Exception()

        viewModel.process(DogAction.OnError(exception))

        verify(exactly = 1) { mockObserver.onChanged(DogState.Error(exception)) }
    }

    @Test
    fun `process GetDog action should emit OnLoaded state`() {
        viewModel.process(DogAction.GetDog)

        verify(exactly = 2) { mockObserver.onChanged(DogState.OnLoaded(mockUrl)) }
    }

    @Test
    fun `restore with state should only emit passed state`() {
        val viewModel = DogViewModel(mockApi, Dispatchers.Default)
        val mockObserver = mockk<Observer<DogState>>(relaxed = true)

        val state = DogState.OnLoaded(mockUrl)
        viewModel.restore(state)
        viewModel.state.observeForever(mockObserver)

        verify(exactly = 0) { mockObserver.onChanged(DogState.Loading) }
        verify(exactly = 1) { mockObserver.onChanged(state) }
    }
}