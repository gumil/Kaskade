package io.gumil.kaskade.sample

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class MusicPlayerTest {

    private val musicPlayer = MusicPlayer()

    @Test
    fun play() {
        assertEquals("Like Ooh-Ahh", musicPlayer.play())
        assertTrue(musicPlayer.isPlaying)
    }

    @Test
    fun pause() {
        musicPlayer.pause()
        assertFalse(musicPlayer.isPlaying)
    }

    @Test
    fun `play then pause then play`() {
        musicPlayer.play()
        musicPlayer.pause()
        assertEquals("Like Ooh-Ahh", musicPlayer.play())
        assertTrue(musicPlayer.isPlaying)
    }

    @Test
    fun next() {
        assertEquals("Like Ooh-Ahh", musicPlayer.next())
        assertTrue(musicPlayer.isPlaying)
    }

    @Test
    fun `next two times`() {
        musicPlayer.next()
        assertEquals("Cheer up", musicPlayer.next())
        assertTrue(musicPlayer.isPlaying)
    }

    @Test
    fun `next until it loops`() {
        val last = (0..15).map { musicPlayer.next() }.last()
        assertEquals("Like Ooh-Ahh", last)
    }

    @Test
    fun previous() {
        assertEquals("The Best Thing I Ever Did", musicPlayer.previous())
        assertTrue(musicPlayer.isPlaying)
    }

    @Test
    fun `previous two times`() {
        musicPlayer.previous()
        assertEquals("Yes Or Yes", musicPlayer.previous())
        assertTrue(musicPlayer.isPlaying)
    }

    @Test
    fun `previous until it loops`() {
        val last = (0..15).map { musicPlayer.previous() }.last()
        assertEquals("The Best Thing I Ever Did", last)
    }

    @Test
    fun stop() {
        musicPlayer.stop()
        assertFalse(musicPlayer.isPlaying)
    }

    @Test
    fun `stop next next stop play`() {
        musicPlayer.stop()
        musicPlayer.next()
        musicPlayer.next()
        musicPlayer.stop()
        assertEquals("Like Ooh-Ahh", musicPlayer.play())
        assertTrue(musicPlayer.isPlaying)
    }
}
