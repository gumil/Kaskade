package dev.gumil.kaskade.sample

import dev.gumil.kaskade.stateEmitter

@Suppress("ComplexMethod", "LabeledExpression")
fun main() {
    println(
        """

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

    """.trimIndent()
    )

    val kaskade = MusicPlayerKaskade().kaskade

    kaskade.stateEmitter().subscribe { state ->
        when (state) {
            is PlayerState.Playing -> println("Now Playing: ${state.music}")
            PlayerState.Stopped -> println("STOPPED")
            PlayerState.Paused -> println("Paused")
        }
    }

    loop@ while (true) {
        val action = readLine()?.get(0) ?: 'q'

        when (action) {
            'z' -> kaskade.dispatch(PlayerAction.Stop)
            'x' -> kaskade.dispatch(PlayerAction.PausePlay)
            'c' -> kaskade.dispatch(PlayerAction.Previous)
            'v' -> kaskade.dispatch(PlayerAction.Next)
            else -> break@loop
        }
    }
}
