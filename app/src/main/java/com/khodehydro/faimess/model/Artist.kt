package com.khodehydro.faimess.model

import androidx.annotation.DrawableRes

/* ---- Artist ---- */

data class Artist(
    val id: String,
    val name: String,
    val initials: String,
    val kind: String,          // "Boy group", "Girl group", "Soloist", "Duo"
    val genre: String,
    val listeners: String,     // "4.8M monthly"
    val followers: String,     // "2.4M followers"
    val following: Boolean,
    val newRelease: Boolean = false,
    val verified: Boolean = false,
    val seed: Int,
    @DrawableRes val photoRes: Int = 0,
)

/* ---- Album ---- */

data class Album(
    val id: String,
    val title: String,
    val artist: String,
    val year: Int,
    val tracks: Int,
    val released: String,      // "Today", "Yesterday", "2 days ago" …
    val seed: Int,
    @DrawableRes val photoRes: Int = 0,
)

/* ---- Library Playlist (curated) ---- */

data class LibraryPlaylist(
    val id: String,
    val name: String,
    val curator: String,
    val tracks: Int,
    val duration: String,
    val mood: String,
    val seed: Int,
    @DrawableRes val photoRes: Int = 0,
)

/* ---- User Playlist (created by user) ---- */

data class UserPlaylist(
    val id: String,
    val name: String,
    val curator: String,
    val mood: String = "Personal Mix",
    val trackIds: List<String> = emptyList(),
    val isDuo: Boolean = false,
    val duoCode: String? = null,
    @DrawableRes val photoRes: Int = 0,
)

/* ---- Fandom ---- */

data class FandomInfo(
    val artistId: String,
    val artistName: String,
    val fandomNameFa: String,
    val fandomNameEn: String,
    val glowColor: String,
    val accentHex: String,
    val mottoFa: String,
)