package com.khodehydro.faimess.model

import androidx.annotation.DrawableRes

data class Person(val id: String, val name: String, @DrawableRes val avatar: Int)

data class Story(
    val person: Person,
    @DrawableRes val image: Int,
    val caption: String,
    val isSelf: Boolean = false,
    val isLive: Boolean = false,
)

data class FeedComment(val author: String, val body: String)

data class FeedPost(
    val id: String,
    val author: Person,
    val time: String,
    val caption: String,
    @DrawableRes val image: Int,
    val imageDescription: String,
    val likes: Int,
    val commentCount: Int,
    val shares: Int,
    val isVideoPreview: Boolean = false,
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

    fun search(posts: List<FeedPost>, query: String): List<FeedPost> {
        val clean = query.trim()
        return posts.filter {
            clean.isEmpty() || it.author.name.contains(clean, ignoreCase = true) ||
                it.caption.contains(clean, ignoreCase = true)
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
