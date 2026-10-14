package com.khodehydro.faimess.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.khodehydro.faimess.data.DemoContent
import com.khodehydro.faimess.model.FeedActions
import com.khodehydro.faimess.model.FeedComment
import com.khodehydro.faimess.model.FeedPost
import com.khodehydro.faimess.model.FeedUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * A deliberately local demo. Small primitive snapshots survive rotation and Android process
 * recreation via SavedStateHandle; this is not permanent storage or a server-backed repository.
 */
class FeedViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _uiState = MutableStateFlow(restoreState())
    val uiState = _uiState.asStateFlow()

    fun toggleLike(postId: String) {
        updatePost(postId, FeedActions::toggleLike)
        savedStateHandle[LIKED_IDS] = ArrayList(_uiState.value.posts.filter { it.isLiked }.map { it.id })
    }

    fun toggleSave(postId: String) {
        updatePost(postId, FeedActions::toggleSave)
        savedStateHandle[SAVED_IDS] = ArrayList(_uiState.value.posts.filter { it.isSaved }.map { it.id })
    }

    fun addComment(postId: String, text: String): Boolean {
        val clean = text.trim()
        val state = _uiState.value
        val post = state.posts.find { it.id == postId } ?: return false
        if (clean.isBlank() || clean.length > FeedActions.MAX_COMMENT_LENGTH ||
            state.posts.sumOf { it.localComments.size } >= FeedActions.MAX_LOCAL_COMMENTS
        ) return false
        val updated = FeedActions.withComment(post, clean)
        updatePost(postId) { updated }
        savedStateHandle["comments_$postId"] = ArrayList(updated.localComments.map { it.body })
        return true
    }

    fun publishPost(text: String): Boolean {
        val clean = text.trim()
        val captions = localCaptions()
        if (clean.isBlank() || clean.length > FeedActions.MAX_CAPTION_LENGTH ||
            captions.size >= FeedActions.MAX_LOCAL_POSTS
        ) return false
        val post = DemoContent.localPost("local-${captions.size}", clean)
        savedStateHandle[LOCAL_CAPTIONS] = ArrayList(captions + clean)
        _uiState.update { it.copy(posts = listOf(post) + it.posts) }
        return true
    }

    fun publishStory(text: String): Boolean {
        val clean = text.trim()
        if (clean.isBlank() || clean.length > FeedActions.MAX_CAPTION_LENGTH) return false
        savedStateHandle[MY_STORY] = clean
        _uiState.update { state ->
            state.copy(stories = state.stories.map { if (it.isSelf) it.copy(caption = clean) else it })
        }
        return true
    }

    fun readNotifications() {
        savedStateHandle[NOTIFICATIONS_READ] = true
        _uiState.update { it.copy(hasUnreadNotifications = false) }
    }

    private fun updatePost(id: String, transform: (FeedPost) -> FeedPost) {
        _uiState.update { state -> state.copy(posts = state.posts.map { if (it.id == id) transform(it) else it }) }
    }

    private fun localCaptions(): List<String> =
        savedStateHandle.get<ArrayList<String>>(LOCAL_CAPTIONS).orEmpty()

    private fun restoreState(): FeedUiState {
        val likedIds = savedStateHandle.get<ArrayList<String>>(LIKED_IDS)?.toSet()
            ?: DemoContent.posts.filter { it.isLiked }.map { it.id }.toSet()
        val savedIds = savedStateHandle.get<ArrayList<String>>(SAVED_IDS).orEmpty().toSet()
        val localPosts = localCaptions().mapIndexed { index, caption ->
            DemoContent.localPost("local-$index", caption)
        }.reversed()
        val posts = (localPosts + DemoContent.posts).map { post ->
            val isLiked = post.id in likedIds
            val comments = savedStateHandle.get<ArrayList<String>>("comments_${post.id}")
                .orEmpty().map { FeedComment("You", it) }
            post.copy(
                isLiked = isLiked,
                likes = post.likes + (if (isLiked) 1 else 0) - (if (post.isLiked) 1 else 0),
                isSaved = post.id in savedIds,
                localComments = comments,
                commentCount = post.commentCount + comments.size,
            )
        }
        val myStory = savedStateHandle.get<String>(MY_STORY)
        return FeedUiState(
            posts = posts,
            stories = DemoContent.stories.map {
                if (it.isSelf && myStory != null) it.copy(caption = myStory) else it
            },
            hasUnreadNotifications = savedStateHandle.get<Boolean>(NOTIFICATIONS_READ) != true,
        )
    }

    private companion object {
        const val LIKED_IDS = "liked_ids"
        const val SAVED_IDS = "saved_ids"
        const val LOCAL_CAPTIONS = "local_captions"
        const val MY_STORY = "my_story"
        const val NOTIFICATIONS_READ = "notifications_read"
    }
}
