package com.khodehydro.faimess.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.khodehydro.faimess.R
import com.khodehydro.faimess.model.FeedPost
import com.khodehydro.faimess.ui.components.Avatar
import com.khodehydro.faimess.ui.components.EmptyState
import com.khodehydro.faimess.ui.theme.MutedInk

@Composable
fun DiscoverScreen(posts: List<FeedPost>, onMedia: (FeedPost) -> Unit, modifier: Modifier = Modifier) {
    var savedOnly by rememberSaveable { mutableStateOf(false) }
    val visiblePosts = if (savedOnly) posts.filter { it.isSaved } else posts
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.testTag("discover_screen"),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 18.dp, bottom = 124.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text(stringResource(R.string.discover_title), style = MaterialTheme.typography.headlineLarge)
                Text(stringResource(R.string.discover_subtitle), style = MaterialTheme.typography.bodyMedium, color = MutedInk)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = !savedOnly, onClick = { savedOnly = false }, label = { Text(stringResource(R.string.for_you)) })
                    FilterChip(selected = savedOnly, onClick = { savedOnly = true }, label = { Text(stringResource(R.string.saved)) })
                }
            }
        }
        if (visiblePosts.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                EmptyState(R.drawable.ic_bookmark, stringResource(R.string.no_saved), stringResource(R.string.no_saved_description))
            }
        }
        items(visiblePosts, key = { it.id }) { post ->
            Box(
                Modifier.fillMaxWidth().aspectRatio(0.78f).clip(RoundedCornerShape(24.dp))
                    .clickable(role = Role.Button, onClick = { onMedia(post) }),
            ) {
                Image(painterResource(post.image), post.imageDescription, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.45f)))))
                Row(
                    Modifier.align(Alignment.BottomStart).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Avatar(post.author, 24.dp)
                    Text(post.author.name, style = MaterialTheme.typography.labelMedium, color = Color.White)
                }
            }
        }
    }
}
