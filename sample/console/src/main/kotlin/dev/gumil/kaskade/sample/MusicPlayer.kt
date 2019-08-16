package dev.gumil.kaskade.sample

internal class MusicPlayer {

    private val playlist = listOf(
        "Like Ooh-Ahh",
        "Cheer up",
        "TT",
        "Knock Knock",
        "Signal",
        "One More Time",
        "Likey",
        "Heart Shaker",
        "Candy Pop",
        "What Is Love?",
        "Wake Me Up",
        "Dance The Night Away",
        "BDZ",
        "Yes Or Yes",
        "The Best Thing I Ever Did"
    )

    private var index = -1

    var isPlaying = false

    fun play(): String {
        if (index == -1) index = 0
        isPlaying = true
        return playlist[index]
    }

    fun pause() {
        isPlaying = false
    }

    fun next(): String {
        isPlaying = true
        return playlist[++index % playlist.size]
    }

    fun previous(): String {
        isPlaying = true
        if (--index < 0) {
            index = playlist.size - 1
        }
        return playlist[index % playlist.size]
    }

    fun stop() {
        isPlaying = false
        index = -1
    }
}
