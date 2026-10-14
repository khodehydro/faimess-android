package com.khodehydro.faimess

import com.khodehydro.faimess.data.DemoContent
import com.khodehydro.faimess.model.FeedActions
import com.khodehydro.faimess.model.compactCount
import org.junit.Assert.*
import org.junit.Test

class FeedActionsTest {
    @Test fun `like and unlike are reversible without changing the original post`() {
        val original = DemoContent.posts.first()
        val unliked = FeedActions.toggleLike(original)
        assertFalse(unliked.isLiked)
        assertEquals(25_999, unliked.likes)
        assertEquals(original, FeedActions.toggleLike(unliked))
        assertEquals(26_000, original.likes)
    }

    @Test fun `likes never become negative`() {
        val inconsistent = DemoContent.posts.first().copy(likes = 0, isLiked = true)
        assertEquals(0, FeedActions.toggleLike(inconsistent).likes)
    }

    @Test fun `bookmark is reversible and does not affect engagement`() {
        val post = DemoContent.posts.first()
        val saved = FeedActions.toggleSave(post)
        assertTrue(saved.isSaved)
        assertEquals(post.likes, saved.likes)
        assertEquals(post, FeedActions.toggleSave(saved))
    }

    @Test fun `comments are trimmed and increment the sample count`() {
        val post = DemoContent.posts.first()
        val commented = FeedActions.withComment(post, "  Beautiful view!  ")
        assertEquals(post.commentCount + 1, commented.commentCount)
        assertEquals("Beautiful view!", commented.localComments.single().body)
        assertTrue(post.localComments.isEmpty())
    }

    @Test fun `blank and overly long comments are ignored`() {
        val post = DemoContent.posts.first()
        assertEquals(post, FeedActions.withComment(post, " \n\t "))
        assertEquals(post, FeedActions.withComment(post, "x".repeat(FeedActions.MAX_COMMENT_LENGTH + 1)))
    }

    @Test fun `search matches names and captions without case sensitivity`() {
        assertEquals(listOf(DemoContent.posts.first()), FeedActions.search(DemoContent.posts, " SIENNA "))
        assertEquals(listOf(DemoContent.posts[1]), FeedActions.search(DemoContent.posts, "LAKE"))
        assertEquals(DemoContent.posts, FeedActions.search(DemoContent.posts, " "))
        assertTrue(FeedActions.search(DemoContent.posts, "no-such-place").isEmpty())
    }

    @Test fun `counts use compact predictable labels at boundaries`() {
        mapOf(
            -1 to "0", 0 to "0", 999 to "999", 1_000 to "1K", 1_250 to "1.2K",
            9_999 to "9.9K", 10_000 to "10K", 26_000 to "26K", 999_999 to "999K",
            1_000_000 to "1M", 1_200_000 to "1.2M",
        ).forEach { (count, expected) -> assertEquals(expected, compactCount(count)) }
    }
}
