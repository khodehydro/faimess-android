package com.khodehydro.faimess

import androidx.lifecycle.SavedStateHandle
import com.khodehydro.faimess.model.FeedActions
import com.khodehydro.faimess.ui.FeedViewModel
import org.junit.Assert.*
import org.junit.Test

class FeedViewModelTest {
    @Test fun `reference fixtures are available immediately without a network`() {
        val viewModel = FeedViewModel(SavedStateHandle())
        assertEquals(3, viewModel.uiState.value.posts.size)
        assertEquals(6, viewModel.uiState.value.stories.size)
        assertEquals("Sienna", viewModel.uiState.value.posts.first().author.name)
    }

    @Test fun `local edits restore from a new saved state handle`() {
        val handle = SavedStateHandle()
        val original = FeedViewModel(handle)
        original.toggleLike("sienna-ridge")
        original.toggleSave("sienna-ridge")
        assertTrue(original.addComment("sienna-ridge", "Worth the climb"))
        assertTrue(original.publishPost("My little adventure"))
        original.toggleLike("local-0")
        original.toggleSave("local-0")
        assertTrue(original.addComment("local-0", "First comment"))
        assertTrue(original.publishStory("A fresh start"))
        original.readNotifications()

        val snapshot = handle.keys().associateWith { handle.get<Any>(it) }
        val restored = FeedViewModel(SavedStateHandle(snapshot))
        assertEquals(original.uiState.value, restored.uiState.value)
    }

    @Test fun `new posts appear first and cannot be empty or too long`() {
        val viewModel = FeedViewModel(SavedStateHandle())
        assertFalse(viewModel.publishPost("  "))
        assertFalse(viewModel.publishPost("x".repeat(FeedActions.MAX_CAPTION_LENGTH + 1)))
        assertTrue(viewModel.publishPost("  Hello green world  "))
        val post = viewModel.uiState.value.posts.first()
        assertEquals("Hello green world", post.caption)
        assertEquals("Just now", post.time)
        assertEquals(0, post.likes)
        assertEquals(4, viewModel.uiState.value.posts.size)
    }

    @Test fun `unknown post actions do not modify visible posts`() {
        val viewModel = FeedViewModel(SavedStateHandle())
        val before = viewModel.uiState.value
        viewModel.toggleLike("unknown")
        viewModel.toggleSave("unknown")
        assertFalse(viewModel.addComment("unknown", "Hello"))
        assertEquals(before, viewModel.uiState.value)
    }

    @Test fun `local post and comment limits bound the saved instance state`() {
        val viewModel = FeedViewModel(SavedStateHandle())
        repeat(FeedActions.MAX_LOCAL_POSTS) { assertTrue(viewModel.publishPost("Post $it")) }
        assertFalse(viewModel.publishPost("One too many"))
        repeat(FeedActions.MAX_LOCAL_COMMENTS) { assertTrue(viewModel.addComment("sienna-ridge", "Comment $it")) }
        assertFalse(viewModel.addComment("sienna-ridge", "One too many"))
    }

    @Test fun `rapid like and save taps update immediately without losing events`() {
        val handle = SavedStateHandle()
        val viewModel = FeedViewModel(handle)
        repeat(101) { index ->
            viewModel.toggleLike("sienna-ridge")
            viewModel.toggleSave("sienna-ridge")
            val post = viewModel.uiState.value.posts.first()
            assertEquals(index % 2 == 1, post.isLiked)
            assertEquals(if (post.isLiked) 26_000 else 25_999, post.likes)
            assertEquals(index % 2 == 0, post.isSaved)
        }
        val restored = FeedViewModel(SavedStateHandle(handle.keys().associateWith { handle.get<Any>(it) }))
        assertEquals(viewModel.uiState.value, restored.uiState.value)
    }

    @Test fun `engagement changes keep unrelated posts stories and media inputs unchanged`() {
        val viewModel = FeedViewModel(SavedStateHandle())
        val before = viewModel.uiState.value
        viewModel.toggleLike("sienna-ridge")
        val after = viewModel.uiState.value
        assertSame(before.stories, after.stories)
        assertSame(before.posts[1], after.posts[1])
        assertSame(before.posts[2], after.posts[2])
        assertSame(before.posts.first().author, after.posts.first().author)
        assertSame(before.posts.first().caption, after.posts.first().caption)
        assertEquals(before.posts.first().image, after.posts.first().image)
    }

    @Test fun `empty stories cannot replace the current story`() {
        val viewModel = FeedViewModel(SavedStateHandle())
        val before = viewModel.uiState.value.stories
        assertFalse(viewModel.publishStory("  "))
        assertEquals(before, viewModel.uiState.value.stories)
    }
}
