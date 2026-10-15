package com.khodehydro.faimess.model

import androidx.annotation.DrawableRes

/* ---- Audio Track ---- */

data class AudioTrack(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Int,          // seconds
    val plays: Long = 0,
    val isSingle: Boolean = false,
    val streamUrl: String = "",
    @DrawableRes val photoRes: Int = 0,
)

/* ---- Banner ---- */

// Banner already in Social.kt

data class TrackLyrics(
    val trackId: String,
    val lines: List<LyricLine>,
)

/* ---- Player Queue ---- */

data class PlayerState(
    val currentTrack: AudioTrack? = null,
    val queue: List<AudioTrack> = emptyList(),
    val isPlaying: Boolean = false,
    val position: Float = 0f,
    val shuffleOn: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val isFavourite: Boolean = false,
    val lyrics: List<LyricLine>? = null,
    val currentLyricIndex: Int = -1,
    val hasTrack: Boolean = currentTrack != null,
)

enum class RepeatMode { OFF, ALL, ONE }

/* ---- Leaderboard entry ---- */

data class LeaderboardEntry(
    val rank: Int,
    val username: String,
    val displayName: String,
    val avatarSeed: Int,
    val points: Int,
    val tier: String,
    val badgeCount: Int,
)