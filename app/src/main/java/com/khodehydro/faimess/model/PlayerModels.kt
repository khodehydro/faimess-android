package com.khodehydro.faimess.model

import androidx.compose.runtime.Immutable
import kotlin.random.Random

/** `off → all → one`, in the order the website's repeat pill cycles through. */
enum class RepeatMode { Off, All, One }

/**
 * One line of the bilingual lyric sheet: the original (Korean with English hooks), its Persian
 * translation and the second it lands on — the shape `/api/tracks/{id}` returns for lyrics.
 */
@Immutable
data class LyricLine(val at: Int, val ko: String, val fa: String)

/**
 * Everything the mini player and the player page render. Mirrors the website's `PlayerContext`:
 * one queue (the list the listener tapped in), a position inside it, the clock, repeat/shuffle
 * and the liked set. [queue] is the order songs actually play in, so shuffling reorders it.
 */
@Immutable
data class PlayerState(
    val queue: List<Track> = emptyList(),
    val index: Int = -1,
    /** where the queue came from, e.g. the playlist name; shown on the player page. */
    val queueLabel: String = "",
    val playing: Boolean = false,
    val positionMs: Int = 0,
    val durationMs: Int = 0,
    val repeat: RepeatMode = RepeatMode.Off,
    val shuffle: Boolean = false,
    val likedIds: Set<String> = PlayerLogic.SEED_LIKES,
) {
    val track: Track? get() = queue.getOrNull(index)
    val progress: Float get() = if (durationMs > 0) (positionMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f
    val remainingMs: Int get() = (durationMs - positionMs).coerceAtLeast(0)
    val isLiked: Boolean get() = track?.let { it.id in likedIds } ?: false
}

/** The queue rules, shared by the ViewModel and the unit tests. */
object PlayerLogic {
    /** The website seeds the liked list with these three songs. */
    val SEED_LIKES: Set<String> = setOf("nt1", "tr3", "tr5")

    /** Playback shorter than this restarts the song on "previous" instead of going back. */
    const val RESTART_THRESHOLD_MS = 3_000

    /**
     * The index to play after [state]'s song ends ([manual] = false) or the listener taps next
     * ([manual] = true). `null` means playback stops: repeat is off and the queue is exhausted.
     * A manual tap wraps around even with repeat off, like the site's next button.
     */
    fun nextIndex(state: PlayerState, manual: Boolean): Int? {
        if (state.queue.isEmpty()) return null
        if (!manual && state.repeat == RepeatMode.One) return state.index
        val next = state.index + 1
        return when {
            next < state.queue.size -> next
            manual || state.repeat == RepeatMode.All -> 0
            else -> null
        }
    }

    /** Previous song, wrapping to the end — the caller decides whether to restart instead. */
    fun previousIndex(state: PlayerState): Int {
        if (state.queue.isEmpty()) return -1
        return if (state.index > 0) state.index - 1 else state.queue.lastIndex
    }

    fun nextRepeat(mode: RepeatMode): RepeatMode = when (mode) {
        RepeatMode.Off -> RepeatMode.All
        RepeatMode.All -> RepeatMode.One
        RepeatMode.One -> RepeatMode.Off
    }

    /**
     * A Fisher–Yates shuffle that keeps [current] first, so turning shuffle on never interrupts
     * the song that is playing.
     */
    fun shuffled(queue: List<Track>, current: Track?, random: Random = Random.Default): List<Track> {
        val rest = queue.filter { it.id != current?.id }.toMutableList()
        for (i in rest.lastIndex downTo 1) {
            val j = random.nextInt(i + 1)
            rest[i] = rest[j].also { rest[j] = rest[i] }
        }
        return listOfNotNull(current) + rest
    }

    /** The lyric line playing at [positionSeconds]: the last line whose start has passed. */
    fun activeLine(lines: List<LyricLine>, positionSeconds: Int): Int {
        var index = 0
        for (i in lines.indices) {
            if (positionSeconds >= lines[i].at) index = i else break
        }
        return index
    }
}
