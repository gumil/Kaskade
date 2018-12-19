package io.gumil.kaskade.sample

import io.gumil.kaskade.stateFlow
import kotlin.test.Test
import kotlin.test.assertEquals

internal class PlayerKaskadeTest {

    @Test
    fun `action play should emit Playing state`() {
        val kaskade = MusicPlayerKaskade().kaskade
        kaskade.stateFlow().subscribe {
            assertEquals(PlayerState.Stopped, it)
        }

        kaskade.stateFlow().subscribe {
            assertEquals(PlayerState.Playing("Like Ooh-Ahh"), it)
        }

        kaskade.process(PlayerAction.PausePlay)
    }

    @Test
    fun `action pause should emit Paused state`() {
        val kaskade = MusicPlayerKaskade().kaskade
        kaskade.stateFlow().subscribe {
            assertEquals(PlayerState.Stopped, it)
        }

        var counter = 0
        kaskade.stateFlow().subscribe {
            if (counter++ == 0) {
                assertEquals(PlayerState.Playing("Like Ooh-Ahh"), it)
                return@subscribe
            }
            assertEquals(PlayerState.Paused, it)
        }

        kaskade.process(PlayerAction.PausePlay)
        kaskade.process(PlayerAction.PausePlay)
    }

    @Test
    fun `action next should emit Playing state`() {
        val kaskade = MusicPlayerKaskade().kaskade
        kaskade.stateFlow().subscribe {
            assertEquals(PlayerState.Stopped, it)
        }

        kaskade.stateFlow().subscribe {
            assertEquals(PlayerState.Playing("Like Ooh-Ahh"), it)
        }

        kaskade.process(PlayerAction.Next)
    }

    @Test
    fun `action previous should emit Playing state`() {
        val kaskade = MusicPlayerKaskade().kaskade
        kaskade.stateFlow().subscribe {
            assertEquals(PlayerState.Stopped, it)
        }

        kaskade.stateFlow().subscribe {
            assertEquals(PlayerState.Playing("The Best Thing I Ever Did"), it)
        }

        kaskade.process(PlayerAction.Previous)
    }

    @Test
    fun `action stop should emit Stopped state`() {
        val kaskade = MusicPlayerKaskade().kaskade
        kaskade.stateFlow().subscribe {
            assertEquals(PlayerState.Stopped, it)
        }

        kaskade.stateFlow().subscribe {
            assertEquals(PlayerState.Stopped, it)
        }

        kaskade.process(PlayerAction.Stop)
    }
}