package com.khodehydro.faimess.model

/* ---- News ---- */

data class NewsAuthor(
    val name: String,
    val role: String,
    val avatarSeed: Int,
)

data class NewsComment(
    val id: String,
    val author: String,
    val handle: String,
    val avatarSeed: Int,
    val time: String,
    val text: String,
    val likes: Int,
    val liked: Boolean = false,
    val reported: Boolean = false,
    val replies: List<NewsComment> = emptyList(),
)

data class NewsItem(
    val id: String,
    val title: String,
    val tag: String,           // "Comeback", "Tour", "Charts", "Editorial", "Awards"
    val source: String,
    val ago: String,
    val seed: Int,
    val excerpt: String,
    @androidx.annotation.DrawableRes val photoRes: Int = 0,
    val author: NewsAuthor,
    val likes: Int,
    val views: Int,
    val commentsCount: Int,
    val bodyKeys: List<String> = emptyList(),
    val comments: List<NewsComment> = emptyList(),
)

/* ---- Comment (for tracks / playlists) ---- */

data class TrackComment(
    val id: String,
    val sourceType: String,    // "track", "playlist", "news"
    val targetId: String,
    val targetTitle: String,
    val author: String,
    val handle: String,
    val avatarSeed: Int,
    val text: String,
    val timeSeconds: Double? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val likes: Int = 0,
    val liked: Boolean = false,
    val replies: List<TrackComment> = emptyList(),
)

/* ---- Report ---- */

data class ContentReport(
    val id: String,
    val targetType: String,    // "comment", "playlist", "user"
    val targetId: String,
    val reason: String,
    val reporterUsername: String,
    val createdAt: Long = System.currentTimeMillis(),
)