package com.khodehydro.faimess.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable

@Immutable
data class Person(val id: String, val name: String, @DrawableRes val avatar: Int)

@Immutable
data class Story(
    val person: Person,
    @DrawableRes val image: Int,
    val caption: String,
    val isSelf: Boolean = false,
    val isLive: Boolean = false,
)

@Immutable
data class FeedComment(val author: String, val body: String)

/** Mirrors the FAIMESS backend's track row (`/api/tracks`): id, title, artist, album, duration. */
@Immutable
data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val seconds: Int,
    @DrawableRes val cover: Int,
)

/**
 * A listener-made playlist, as the FAIMESS backend stores it (`playlists`: name, mood, one of the
 * site's bundled covers, and the tracks it references). The post that shares it names the owner.
 */
@Immutable
data class Playlist(
    val id: String,
    val name: String,
    val mood: String,
    @DrawableRes val cover: Int,
    val tracks: List<Track>,
) {
    val totalSeconds: Int get() = tracks.sumOf { it.seconds }
}

data class FeedPost(
    val id: String,
    val author: Person,
    val time: String,
    val caption: String,
    val playlist: Playlist,
    val likes: Int,
    val commentCount: Int,
    val shares: Int,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val localComments: List<FeedComment> = emptyList(),
)

data class FeedUiState(
    val posts: List<FeedPost>,
    val stories: List<Story>,
    val hasUnreadNotifications: Boolean = true,
)

/** Pure transformations keep the demo's behavior independently testable. */
object FeedActions {
    const val MAX_CAPTION_LENGTH = 500
    const val MAX_COMMENT_LENGTH = 280
    const val MAX_LOCAL_POSTS = 20
    const val MAX_LOCAL_COMMENTS = 50

    fun toggleLike(post: FeedPost): FeedPost = post.copy(
        isLiked = !post.isLiked,
        likes = (post.likes + if (post.isLiked) -1 else 1).coerceAtLeast(0),
    )

    fun toggleSave(post: FeedPost): FeedPost = post.copy(isSaved = !post.isSaved)

    fun withComment(post: FeedPost, body: String): FeedPost {
        val clean = body.trim()
        if (clean.isBlank() || clean.length > MAX_COMMENT_LENGTH) return post
        return post.copy(
            commentCount = post.commentCount + 1,
            localComments = post.localComments + FeedComment("You", clean),
        )
    }

    /** Matches the owner, the caption, the playlist's name or mood, and any track title or artist. */
    fun search(posts: List<FeedPost>, query: String): List<FeedPost> {
        val clean = query.trim()
        if (clean.isEmpty()) return posts
        return posts.filter { post ->
            post.author.name.contains(clean, ignoreCase = true) ||
                post.caption.contains(clean, ignoreCase = true) ||
                post.playlist.name.contains(clean, ignoreCase = true) ||
                post.playlist.mood.contains(clean, ignoreCase = true) ||
                post.playlist.tracks.any {
                    it.title.contains(clean, ignoreCase = true) || it.artist.contains(clean, ignoreCase = true)
                }
        }
    }
}

/** Deterministic English labels, even when the device uses a different locale. */
fun compactCount(count: Int): String = when {
    count >= 1_000_000 -> compactUnit(count, 1_000_000, "M")
    count >= 1_000 -> compactUnit(count, 1_000, "K")
    else -> count.coerceAtLeast(0).toString()
}

private fun compactUnit(count: Int, divisor: Int, suffix: String): String {
    val whole = count / divisor
    val tenth = (count % divisor) / (divisor / 10)
    return if (whole < 10 && tenth > 0) "$whole.$tenth$suffix" else "$whole$suffix"
}

/** "3:28" — the track-row clock the site prints next to every song. */
fun formatClock(seconds: Int): String {
    val total = seconds.coerceAtLeast(0)
    return "${total / 60}:${(total % 60).toString().padStart(2, '0')}"
}

/** "17 min" / "1 h 12 min" — a playlist's length, never shorter than one minute. */
fun formatLength(seconds: Int): String {
    val minutes = ((seconds.coerceAtLeast(0) + 30) / 60).coerceAtLeast(1)
    return if (minutes < 60) "$minutes min" else "${minutes / 60} h ${minutes % 60} min"
}
