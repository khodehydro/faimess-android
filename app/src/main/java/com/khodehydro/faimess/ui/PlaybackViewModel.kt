package com.khodehydro.faimess.ui

import android.media.MediaPlayer
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khodehydro.faimess.R
import com.khodehydro.faimess.model.LyricLine
import com.khodehydro.faimess.model.LyricsData
import com.khodehydro.faimess.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Playback state that drives both the mini-player bar and the full-screen player.
 *
 * One demo audio file (raw/faimess_demo) stands in for every track — matching the
 * FAIMESS web app's own approach (docs/audio.md).  The [track], [playing] and
 * [positionMs] fields are what the UI observes.
 */
@Immutable
data class PlayerUiState(
    val track: Track? = null,
    val playlist: List<Track> = emptyList(),
    val queueIndex: Int = -1,
    val playing: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val shuffle: Boolean = false,
    val repeat: RepeatMode = RepeatMode.Off,
    val isFavourite: Boolean = false,
    /** Current lyric sheet for the playing track, null if none exists. */
    val lyrics: List<LyricLine>? = null,
    /** Index of the active (highlighted) lyric line, -1 if no lyrics. */
    val activeLyricIndex: Int = -1,
) {
    val hasTrack: Boolean get() = track != null
    val progress: Float get() = if (durationMs > 0) (positionMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f
    val positionSec: Int get() = (positionMs / 1000).toInt()
    val durationSec: Int get() = (durationMs / 1000).toInt()
}

enum class RepeatMode { Off, All, One }

class PlaybackViewModel : ViewModel() {

    private val _state = MutableStateFlow(PlayerUiState())
    val state = _state.asStateFlow()

    private var player: MediaPlayer? = null
    private var progressJob: Job? = null

    /* ---- public API --------------------------------------------------- */

    /** Start playing a track (from a playlist or single). */
    fun play(track: Track, playlist: List<Track> = listOf(track)) {
        val index = playlist.indexOf(track).coerceAtLeast(0)
        val lyrics = LyricsData.lyricsFor(track.id)
        _state.update {
            it.copy(
                track = track,
                playlist = playlist,
                queueIndex = index,
                isFavourite = false,
                lyrics = lyrics,
                activeLyricIndex = -1,
            )
        }
        startPlayback()
    }

    fun togglePlayPause() {
        val p = player ?: return
        if (p.isPlaying) {
            p.pause()
            _state.update { it.copy(playing = false) }
            stopProgressPolling()
        } else {
            p.start()
            _state.update { it.copy(playing = true) }
            startProgressPolling()
        }
    }

    fun seekTo(fraction: Float) {
        val p = player ?: return
        val target = (fraction * p.duration).toInt().coerceIn(0, p.duration)
        p.seekTo(target)
        _state.update { it.copy(positionMs = target.toLong()) }
        updateActiveLyric()
    }

    fun seekToSeconds(seconds: Int) {
        val p = player ?: return
        val target = (seconds * 1000).coerceIn(0, p.duration)
        p.seekTo(target)
        _state.update { it.copy(positionMs = target.toLong()) }
        updateActiveLyric()
    }

    fun next() {
        val s = _state.value
        if (s.playlist.isEmpty()) return
        val nextIndex = when {
            s.shuffle -> (0 until s.playlist.size).random()
            else -> (s.queueIndex + 1) % s.playlist.size
        }
        val track = s.playlist[nextIndex]
        val lyrics = LyricsData.lyricsFor(track.id)
        _state.update { it.copy(queueIndex = nextIndex, track = track, lyrics = lyrics, activeLyricIndex = -1) }
        startPlayback()
    }

    fun previous() {
        val s = _state.value
        if (s.playlist.isEmpty()) return
        // If we're past 3 seconds, restart current track; else go back
        val p = player
        if (p != null && p.currentPosition > 3000) {
            p.seekTo(0)
            _state.update { it.copy(positionMs = 0L) }
            updateActiveLyric()
            return
        }
        val prevIndex = if (s.queueIndex > 0) s.queueIndex - 1 else s.playlist.size - 1
        val track = s.playlist[prevIndex]
        val lyrics = LyricsData.lyricsFor(track.id)
        _state.update { it.copy(queueIndex = prevIndex, track = track, lyrics = lyrics, activeLyricIndex = -1) }
        startPlayback()
    }

    fun toggleShuffle() {
        _state.update { it.copy(shuffle = !it.shuffle) }
    }

    fun toggleRepeat() {
        _state.update {
            it.copy(
                repeat = when (it.repeat) {
                    RepeatMode.Off -> RepeatMode.All
                    RepeatMode.All -> RepeatMode.One
                    RepeatMode.One -> RepeatMode.Off
                }
            )
        }
    }

    fun toggleFavourite() {
        _state.update { it.copy(isFavourite = !it.isFavourite) }
    }

    fun playTrackInQueue(track: Track) {
        val s = _state.value
        val index = s.playlist.indexOf(track)
        if (index >= 0) {
            val lyrics = LyricsData.lyricsFor(track.id)
            _state.update { it.copy(queueIndex = index, track = track, lyrics = lyrics, activeLyricIndex = -1) }
            startPlayback()
        } else {
            play(track, s.playlist)
        }
    }

    fun stop() {
        releasePlayer()
        _state.update {
            PlayerUiState(
                playlist = it.playlist,
                shuffle = it.shuffle,
                repeat = it.repeat,
            )
        }
    }

    /* ---- private ------------------------------------------------------ */

    private fun startPlayback() {
        releasePlayer()
        val ctx = com.khodehydro.faimess.FaimessAppContextHolder.appContext
        player = MediaPlayer.create(ctx, R.raw.faimess_demo).apply {
            setOnCompletionListener { onTrackFinished() }
            start()
        }
        _state.update {
            it.copy(
                playing = true,
                positionMs = 0L,
                durationMs = player?.duration?.toLong() ?: 0L,
            )
        }
        startProgressPolling()
    }

    private fun onTrackFinished() {
        val s = _state.value
        when (s.repeat) {
            RepeatMode.One -> {
                player?.seekTo(0)
                player?.start()
                _state.update { it.copy(positionMs = 0L, playing = true) }
                startProgressPolling()
            }
            RepeatMode.All -> next()
            RepeatMode.Off -> {
                if (s.queueIndex < s.playlist.size - 1) next()
                else _state.update { it.copy(playing = false, positionMs = 0L) }
            }
        }
    }

    private fun startProgressPolling() {
        stopProgressPolling()
        progressJob = viewModelScope.launch {
            while (true) {
                delay(250)
                val p = player ?: break
                if (!p.isPlaying) break
                _state.update { it.copy(positionMs = p.currentPosition.toLong()) }
                updateActiveLyric()
            }
        }
    }

    private fun updateActiveLyric() {
        val s = _state.value
        val lines = s.lyrics ?: return
        val sec = s.positionSec
        val newIndex = LyricsData.activeLineIndex(lines, sec)
        if (newIndex != s.activeLyricIndex) {
            _state.update { it.copy(activeLyricIndex = newIndex) }
        }
    }

    private fun stopProgressPolling() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun releasePlayer() {
        stopProgressPolling()
        player?.release()
        player = null
    }

    override fun onCleared() {
        releasePlayer()
        super.onCleared()
    }
}