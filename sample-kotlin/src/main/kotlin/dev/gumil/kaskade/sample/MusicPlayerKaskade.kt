package dev.gumil.kaskade.sample

import dev.gumil.kaskade.Action
import dev.gumil.kaskade.Kaskade
import dev.gumil.kaskade.State

internal class MusicPlayerKaskade {
    private val musicPlayer = MusicPlayer()

    val kaskade = Kaskade.create<PlayerAction, PlayerState>(PlayerState.Stopped) {
        on<PlayerAction.PausePlay> {
            when (state) {
                is PlayerState.Stopped,
                is PlayerState.Paused -> PlayerState.Playing(musicPlayer.play())
                else -> {
                    musicPlayer.pause()
                    PlayerState.Paused
                }
            }
        }

        on<PlayerAction.Next> {
            PlayerState.Playing(musicPlayer.next())
        }

        on<PlayerAction.Previous> {
            PlayerState.Playing(musicPlayer.previous())
        }

        on<PlayerAction.Stop> {
            musicPlayer.stop()
            PlayerState.Stopped
        }
    }
}

sealed class PlayerAction : Action {
    object PausePlay : PlayerAction()
    object Next : PlayerAction()
    object Previous : PlayerAction()
    object Stop : PlayerAction()
}

sealed class PlayerState : State {
    data class Playing(val music: String) : PlayerState()
    object Stopped : PlayerState()
    object Paused : PlayerState()
}
