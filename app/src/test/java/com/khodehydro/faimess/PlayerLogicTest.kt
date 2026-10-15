package com.khodehydro.faimess

import com.khodehydro.faimess.data.DemoContent
import com.khodehydro.faimess.data.DemoLyrics
import com.khodehydro.faimess.model.LyricLine
import com.khodehydro.faimess.model.PlayerLogic
import com.khodehydro.faimess.model.PlayerState
import com.khodehydro.faimess.model.RepeatMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class PlayerLogicTest {
    private val queue = DemoContent.midnightDrive.tracks
    private val lastIndex = queue.lastIndex

    @Test fun `a finished song moves to the next one and stops at the end with repeat off`() {
        assertEquals(1, PlayerLogic.nextIndex(PlayerState(queue, index = 0), manual = false))
        assertNull(PlayerLogic.nextIndex(PlayerState(queue, index = lastIndex), manual = false))
    }

    @Test fun `the next button wraps around even with repeat off`() {
        assertEquals(0, PlayerLogic.nextIndex(PlayerState(queue, index = lastIndex), manual = true))
    }

    @Test fun `repeat all restarts the queue and repeat one replays the song`() {
        assertEquals(0, PlayerLogic.nextIndex(PlayerState(queue, index = lastIndex, repeat = RepeatMode.All), manual = false))
        assertEquals(2, PlayerLogic.nextIndex(PlayerState(queue, index = 2, repeat = RepeatMode.One), manual = false))
        // A tap on next still skips while repeat one is on.
        assertEquals(3, PlayerLogic.nextIndex(PlayerState(queue, index = 2, repeat = RepeatMode.One), manual = true))
    }

    @Test fun `an empty queue has nothing to play`() {
        assertNull(PlayerLogic.nextIndex(PlayerState(), manual = true))
        assertEquals(-1, PlayerLogic.previousIndex(PlayerState()))
    }

    @Test fun `previous steps back and wraps to the end`() {
        assertEquals(1, PlayerLogic.previousIndex(PlayerState(queue, index = 2)))
        assertEquals(lastIndex, PlayerLogic.previousIndex(PlayerState(queue, index = 0)))
    }

    @Test fun `repeat cycles off, all, one like the site's pill`() {
        assertEquals(RepeatMode.All, PlayerLogic.nextRepeat(RepeatMode.Off))
        assertEquals(RepeatMode.One, PlayerLogic.nextRepeat(RepeatMode.All))
        assertEquals(RepeatMode.Off, PlayerLogic.nextRepeat(RepeatMode.One))
    }

    @Test fun `shuffle keeps the playing song first and loses nothing`() {
        val current = queue[3]
        val shuffled = PlayerLogic.shuffled(queue, current, Random(7))
        assertEquals(current, shuffled.first())
        assertEquals(queue.toSet(), shuffled.toSet())
        assertEquals(queue.size, shuffled.size)
        // Without a current song every track is still there exactly once.
        assertEquals(queue.sortedBy { it.id }, PlayerLogic.shuffled(queue, null, Random(1)).sortedBy { it.id })
    }

    @Test fun `shuffle actually reorders a long enough list`() {
        val orders = (1..20).map { seed -> PlayerLogic.shuffled(queue, null, Random(seed)).map { it.id } }
        assertTrue(orders.any { it != queue.map { track -> track.id } })
    }

    @Test fun `the active lyric line is the last one whose start has passed`() {
        val lines = listOf(LyricLine(0, "a", "آ"), LyricLine(12, "b", "ب"), LyricLine(30, "c", "پ"))
        assertEquals(0, PlayerLogic.activeLine(lines, 0))
        assertEquals(0, PlayerLogic.activeLine(lines, 11))
        assertEquals(1, PlayerLogic.activeLine(lines, 12))
        assertEquals(2, PlayerLogic.activeLine(lines, 500))
    }

    @Test fun `player state derives progress, remaining time and the liked seed`() {
        val state = PlayerState(queue, index = 1, positionMs = 40_000, durationMs = 160_000)
        assertEquals(0.25f, state.progress, 0.0001f)
        assertEquals(120_000, state.remainingMs)
        assertEquals("Afterglow", state.track?.title)
        assertTrue(state.isLiked) // nt1 is one of the site's seeded likes
        assertFalse(state.copy(index = 0).isLiked)
        assertEquals(0f, PlayerState().progress, 0f)
        assertNull(PlayerState().track)
    }

    @Test fun `every lyric sheet belongs to a catalogue song and is in time order`() {
        val catalogue = DemoContent.posts.flatMap { it.playlist.tracks }.map { it.id }.toSet()
        assertTrue(DemoLyrics.trackIdsWithLyrics.isNotEmpty())
        DemoLyrics.trackIdsWithLyrics.forEach { id ->
            assertTrue("unknown track $id", id in catalogue)
            val lines = DemoLyrics.forTrack(DemoContent.posts.flatMap { it.playlist.tracks }.first { it.id == id })!!
            assertEquals(lines, lines.sortedBy { it.at })
            assertEquals(0, lines.first().at)
            assertTrue(lines.all { it.ko.isNotBlank() && it.fa.isNotBlank() })
        }
        // Afterimage has no sheet yet, as on the site.
        assertNull(DemoLyrics.forTrack(DemoContent.afterimage))
        assertNull(DemoLyrics.forTrack(null))
    }
}
