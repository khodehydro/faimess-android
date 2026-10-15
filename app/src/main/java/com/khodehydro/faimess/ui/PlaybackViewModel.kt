package com.khodehydro.faimess.ui

import android.media.MediaPlayer
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khodehydro.faimess.R
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
) {
    val hasTrack: Boolean get() = track != null
    val progress: Float get() = if (durationMs > 0) (positionMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f
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
        _state.update {
            it.copy(
                track = track,
                playlist = playlist,
                queueIndex = index,
                isFavourite = false,
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
    }

    fun next() {
        val s = _state.value
        if (s.playlist.isEmpty()) return
        val nextIndex = when {
            s.shuffle -> (0 until s.playlist.size).random()
            else -> (s.queueIndex + 1) % s.playlist.size
        }
        _state.update { it.copy(queueIndex = nextIndex, track = s.playlist[nextIndex]) }
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
            return
        }
        val prevIndex = if (s.queueIndex > 0) s.queueIndex - 1 else s.playlist.size - 1
        _state.update { it.copy(queueIndex = prevIndex, track = s.playlist[prevIndex]) }
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
        val s = _state.value
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
            }
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