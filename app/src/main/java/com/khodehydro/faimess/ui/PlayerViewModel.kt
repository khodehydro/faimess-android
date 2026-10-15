package com.khodehydro.faimess.ui

import android.app.Application
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.khodehydro.faimess.R
import com.khodehydro.faimess.model.PlayerLogic
import com.khodehydro.faimess.model.PlayerState
import com.khodehydro.faimess.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * The listening session, scoped to the Activity so the mini player survives tab changes.
 *
 * Playback goes through Android's [MediaPlayer] on the bundled demo master (`res/raw/faimess_demo`),
 * exactly as the website plays one synthesized file for every song in its catalogue. The seek
 * clock comes from the engine; if the engine cannot start (no decoder, no audio route) the
 * session falls back to a simulated clock so the interface still behaves. Playback stops with the
 * Activity — there is no foreground service or media notification yet.
 */
class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(PlayerState())
    val uiState = _uiState.asStateFlow()

    /** The queue in the order the listener tapped it, kept to restore when shuffle turns off. */
    private var sourceQueue: List<Track> = emptyList()
    private var player: MediaPlayer? = null
    private var engineFailed = false
    private var simulatedAnchor = 0L
    private var ticker: Job? = null
    private var resumeOnFocusGain = false

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()
    private val audioManager = application.getSystemService(AudioManager::class.java)
    private val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
        .setAudioAttributes(audioAttributes)
        .setOnAudioFocusChangeListener { change ->
            when (change) {
                AudioManager.AUDIOFOCUS_LOSS -> { resumeOnFocusGain = false; pause() }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> { resumeOnFocusGain = _uiState.value.playing; pause() }
                AudioManager.AUDIOFOCUS_GAIN -> if (resumeOnFocusGain) { resumeOnFocusGain = false; resume() }
            }
        }
        .build()

    /** Plays [track] inside [queue] (the list it was tapped in); [label] names that list. */
    fun play(track: Track, queue: List<Track> = listOf(track), label: String = "") {
        val source = if (queue.any { it.id == track.id }) queue else listOf(track) + queue
        sourceQueue = source
        val ordered = if (_uiState.value.shuffle) PlayerLogic.shuffled(source, track) else source
        _uiState.update {
            it.copy(queue = ordered, index = ordered.indexOfFirst { item -> item.id == track.id }, queueLabel = label)
        }
        startCurrent()
    }

    /** The playlist page's Play button: the whole list from the top. */
    fun playAll(queue: List<Track>, label: String = "") {
        queue.firstOrNull()?.let { play(it, queue, label) }
    }

    /** The playlist page's Shuffle button: shuffle on, a random song first. */
    fun shuffleAll(queue: List<Track>, label: String = "") {
        if (queue.isEmpty()) return
        _uiState.update { it.copy(shuffle = true) }
        play(PlayerLogic.shuffled(queue, null).first(), queue, label)
    }

    /** Jumps to a song in the current queue (the "Up next" list). */
    fun playAt(index: Int) {
        if (index !in _uiState.value.queue.indices) return
        _uiState.update { it.copy(index = index) }
        startCurrent()
    }

    fun toggle() {
        if (_uiState.value.playing) pause() else resume()
    }

    fun pause() {
        if (!_uiState.value.playing) return
        syncPosition()
        runCatching { player?.takeIf { it.isPlaying }?.pause() }
        _uiState.update { it.copy(playing = false) }
        stopTicker()
        audioManager?.abandonAudioFocusRequest(focusRequest)
    }

    fun resume() {
        val state = _uiState.value
        if (state.track == null || state.playing) return
        if (state.durationMs > 0 && state.positionMs >= state.durationMs) {
            startCurrent()
            return
        }
        val engine = engine()
        if (engine != null) runCatching { engine.start() }.onFailure { dropEngine() }
        simulatedAnchor = SystemClock.elapsedRealtime()
        _uiState.update { it.copy(playing = true) }
        requestFocus()
        startTicker()
    }

    fun next() {
        val index = PlayerLogic.nextIndex(_uiState.value, manual = true) ?: return
        _uiState.update { it.copy(index = index) }
        startCurrent()
    }

    /** Restarts the song when it has played a few seconds, otherwise steps back in the queue. */
    fun previous() {
        val state = _uiState.value
        if (state.track == null) return
        if (state.positionMs > PlayerLogic.RESTART_THRESHOLD_MS || state.queue.size == 1) {
            seekTo(0)
            if (!state.playing) resume()
            return
        }
        _uiState.update { it.copy(index = PlayerLogic.previousIndex(state)) }
        startCurrent()
    }

    fun seekTo(positionMs: Int) {
        val state = _uiState.value
        if (state.track == null) return
        val clamped = positionMs.coerceIn(0, state.durationMs.coerceAtLeast(0))
        runCatching { player?.seekTo(clamped) }
        simulatedAnchor = SystemClock.elapsedRealtime()
        _uiState.update { it.copy(positionMs = clamped) }
    }

    fun seekToFraction(fraction: Float) {
        seekTo((fraction.coerceIn(0f, 1f) * _uiState.value.durationMs).roundToInt())
    }

    fun toggleRepeat() {
        _uiState.update { it.copy(repeat = PlayerLogic.nextRepeat(it.repeat)) }
    }

    fun toggleShuffle() {
        _uiState.update { state ->
            val on = !state.shuffle
            val queue = if (on) PlayerLogic.shuffled(state.queue, state.track) else sourceQueue.ifEmpty { state.queue }
            val index = state.track?.let { current -> queue.indexOfFirst { it.id == current.id } } ?: -1
            state.copy(shuffle = on, queue = queue, index = index)
        }
    }

    /** Returns whether [trackId] is liked after the toggle, so the caller can word its toast. */
    fun toggleLike(trackId: String): Boolean {
        var liked = false
        _uiState.update { state ->
            liked = trackId !in state.likedIds
            state.copy(likedIds = if (liked) state.likedIds + trackId else state.likedIds - trackId)
        }
        return liked
    }

    private fun startCurrent() {
        val track = _uiState.value.track ?: return
        val engine = engine()
        var durationMs = track.seconds * 1000
        if (engine != null) {
            runCatching {
                engine.seekTo(0)
                if (!engine.isPlaying) engine.start()
                durationMs = engine.duration.takeIf { it > 0 } ?: durationMs
            }.onFailure { dropEngine() }
        }
        simulatedAnchor = SystemClock.elapsedRealtime()
        _uiState.update { it.copy(playing = true, positionMs = 0, durationMs = durationMs) }
        requestFocus()
        startTicker()
    }

    private fun onCompleted() {
        val state = _uiState.value
        when (val index = PlayerLogic.nextIndex(state, manual = false)) {
            null -> {
                _uiState.update { it.copy(playing = false, positionMs = it.durationMs) }
                stopTicker()
                audioManager?.abandonAudioFocusRequest(focusRequest)
            }
            else -> {
                _uiState.update { it.copy(index = index) }
                startCurrent()
            }
        }
    }

    private fun engine(): MediaPlayer? {
        if (engineFailed) return null
        player?.let { return it }
        return try {
            MediaPlayer().apply {
                setAudioAttributes(audioAttributes)
                getApplication<Application>().resources.openRawResourceFd(R.raw.faimess_demo).use { source ->
                    setDataSource(source.fileDescriptor, source.startOffset, source.length)
                }
                prepare()
                setOnCompletionListener { onCompleted() }
                setOnErrorListener { _, _, _ -> dropEngine(); true }
            }.also { player = it }
        } catch (_: Exception) {
            engineFailed = true
            null
        }
    }

    /** Keeps the session alive on the simulated clock when the engine gives up. */
    private fun dropEngine() {
        runCatching { player?.release() }
        player = null
        engineFailed = true
        simulatedAnchor = SystemClock.elapsedRealtime()
        _uiState.update { state ->
            if (state.durationMs > 0) state else state.copy(durationMs = (state.track?.seconds ?: 0) * 1000)
        }
    }

    private fun startTicker() {
        if (ticker?.isActive == true) return
        ticker = viewModelScope.launch {
            while (isActive) {
                delay(TICK_MS)
                tick()
            }
        }
    }

    private fun stopTicker() {
        ticker?.cancel()
        ticker = null
    }

    private fun tick() {
        val state = _uiState.value
        if (!state.playing) return
        val engine = player
        if (engine != null && !engineFailed) {
            val position = runCatching { engine.currentPosition }.getOrNull()
            if (position != null) _uiState.update { it.copy(positionMs = position.coerceIn(0, it.durationMs)) }
            return
        }
        val now = SystemClock.elapsedRealtime()
        val position = state.positionMs + (now - simulatedAnchor).toInt()
        simulatedAnchor = now
        if (position >= state.durationMs) {
            _uiState.update { it.copy(positionMs = it.durationMs) }
            onCompleted()
        } else {
            _uiState.update { it.copy(positionMs = position) }
        }
    }

    private fun syncPosition() {
        val engine = player
        if (engine != null && !engineFailed) {
            runCatching { engine.currentPosition }.getOrNull()?.let { position ->
                _uiState.update { it.copy(positionMs = position.coerceIn(0, it.durationMs)) }
            }
        } else {
            val now = SystemClock.elapsedRealtime()
            _uiState.update { it.copy(positionMs = (it.positionMs + (now - simulatedAnchor).toInt()).coerceIn(0, it.durationMs)) }
            simulatedAnchor = now
        }
    }

    private fun requestFocus() {
        audioManager?.requestAudioFocus(focusRequest)
    }

    override fun onCleared() {
        stopTicker()
        runCatching { player?.release() }
        player = null
        audioManager?.abandonAudioFocusRequest(focusRequest)
    }

    private companion object {
        const val TICK_MS = 250L
    }
}
