package io.gumil.kaskade.sample

import io.gumil.kaskade.stateFlow

fun main(args: Array<String>) {
    println("""

        ########################
        # KASKADE MUSIC PLAYER #
        ########################

        ########################
        # controls:            #
        #  z = stop            #
        #  x = pause/play      #
        #  c = previous        #
        #  v = next            #
        #  q = exit            #
        ########################

    """.trimIndent())

    val kaskade = MusicPlayerKaskade().kaskade

    kaskade.stateFlow().subscribe {
        when (it) {
            is PlayerState.Playing -> println("Now Playing: ${it.music}")
            PlayerState.Stopped -> println("STOPPED")
            PlayerState.Paused -> println("Paused")
        }
    }

    loop@while(true) {
        val action = readLine()?.get(0) ?: 'q'

        when (action) {
            'z' -> kaskade.process(PlayerAction.Stop)
            'x' -> kaskade.process(PlayerAction.PausePlay)
            'c' -> kaskade.process(PlayerAction.Previous)
            'v' -> kaskade.process(PlayerAction.Next)
            else -> break@loop
        }
    }
}

