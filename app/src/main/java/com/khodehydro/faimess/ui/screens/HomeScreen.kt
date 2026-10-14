package com.khodehydro.faimess.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.khodehydro.faimess.model.FeedUiState
import com.khodehydro.faimess.model.Story
import com.khodehydro.faimess.ui.components.PostCard
import com.khodehydro.faimess.ui.components.PostInteractions
import com.khodehydro.faimess.ui.components.StoriesRow
import com.khodehydro.faimess.ui.theme.CanvasBackground

@Composable
fun HomeScreen(
    state: FeedUiState,
    listState: LazyListState,
    interactions: PostInteractions,
    onStory: (Story) -> Unit,
    onAddStory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.background(CanvasBackground).testTag("home_feed"),
        state = listState,
        contentPadding = PaddingValues(bottom = 124.dp),
    ) {
        item(key = "stories", contentType = "stories") {
            StoriesRow(state.stories, onStory, onAddStory)
            Spacer(Modifier.height(12.dp))
        }
        items(state.posts, key = { it.id }, contentType = { "post" }) { post ->
            PostCard(post, interactions, Modifier.padding(horizontal = 16.dp).padding(bottom = 12.dp))
        }
    }
}
