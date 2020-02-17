package dev.gumil.kaskade.sample

import dev.gumil.kaskade.stateEmitter
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import kotlin.test.Test

internal class PlayerKaskadeTest {

    @Test
    fun `action play should emit Playing state`() {
        val kaskade = MusicPlayerKaskade().kaskade
        val mockSubscriber = mockk<(PlayerState) -> Unit>()
        every { mockSubscriber.invoke(any()) } returns Unit

        kaskade.stateEmitter().subscribe(mockSubscriber)
        kaskade.dispatch(PlayerAction.PausePlay)

        verify { mockSubscriber.invoke(PlayerState.Playing("Like Ooh-Ahh")) }
        confirmVerified(mockSubscriber)
    }

    @Test
    fun `action pause should emit Paused state`() {
        val kaskade = MusicPlayerKaskade().kaskade
        val mockSubscriber = mockk<(PlayerState) -> Unit>()
        every { mockSubscriber.invoke(any()) } returns Unit

        kaskade.stateEmitter().subscribe(mockSubscriber)
        kaskade.dispatch(PlayerAction.PausePlay)
        kaskade.dispatch(PlayerAction.PausePlay)

        verifyOrder {
            mockSubscriber.invoke(PlayerState.Playing("Like Ooh-Ahh"))
            mockSubscriber.invoke(PlayerState.Paused)
        }
        confirmVerified(mockSubscriber)
    }

    @Test
    fun `action next should emit Playing state`() {
        val kaskade = MusicPlayerKaskade().kaskade
        val mockSubscriber = mockk<(PlayerState) -> Unit>()
        every { mockSubscriber.invoke(any()) } returns Unit

        kaskade.stateEmitter().subscribe(mockSubscriber)
        kaskade.dispatch(PlayerAction.Next)

        verify { mockSubscriber.invoke(PlayerState.Playing("Like Ooh-Ahh")) }
        confirmVerified(mockSubscriber)
    }

    @Test
    fun `action previous should emit Playing state`() {
        val kaskade = MusicPlayerKaskade().kaskade
        val mockSubscriber = mockk<(PlayerState) -> Unit>()
        every { mockSubscriber.invoke(any()) } returns Unit

        kaskade.stateEmitter().subscribe(mockSubscriber)
        kaskade.dispatch(PlayerAction.Previous)

        verify { mockSubscriber.invoke(PlayerState.Playing("The Best Thing I Ever Did")) }
        confirmVerified(mockSubscriber)
    }

    @Test
    fun `action stop should emit Stopped state`() {
        val kaskade = MusicPlayerKaskade().kaskade
        val mockSubscriber = mockk<(PlayerState) -> Unit>()
        every { mockSubscriber.invoke(any()) } returns Unit

        kaskade.stateEmitter().subscribe(mockSubscriber)
        kaskade.dispatch(PlayerAction.Stop)

        verify { mockSubscriber.invoke(PlayerState.Stopped) }
        confirmVerified(mockSubscriber)
    }
}
