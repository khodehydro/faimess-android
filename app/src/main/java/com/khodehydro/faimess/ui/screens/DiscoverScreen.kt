package com.khodehydro.faimess.ui.screens

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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.khodehydro.faimess.R
import com.khodehydro.faimess.model.FeedPost
import com.khodehydro.faimess.ui.components.Avatar
import com.khodehydro.faimess.ui.components.EmptyState
import com.khodehydro.faimess.ui.components.LocalBottomChromeHeight
import com.khodehydro.faimess.ui.components.OfflinePhoto
import com.khodehydro.faimess.ui.theme.InkMuted

@Composable
fun DiscoverScreen(posts: List<FeedPost>, onOpen: (String) -> Unit, modifier: Modifier = Modifier) {
    var savedOnly by rememberSaveable { mutableStateOf(false) }
    val visiblePosts = remember(posts, savedOnly) {
        if (savedOnly) posts.filter { it.isSaved } else posts
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.testTag("discover_screen"),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 18.dp, bottom = LocalBottomChromeHeight.current),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text(stringResource(R.string.discover_title), style = MaterialTheme.typography.headlineLarge)
                Text(stringResource(R.string.discover_subtitle), style = MaterialTheme.typography.bodyMedium, color = InkMuted)
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
        items(visiblePosts, key = { it.id }, contentType = { "playlist_tile" }) { post ->
            val playlist = post.playlist
            val description = stringResource(R.string.open_playlist, playlist.name)
            Box(
                Modifier.fillMaxWidth().aspectRatio(0.78f).clip(RoundedCornerShape(24.dp))
                    .clickable(role = Role.Button, onClick = { onOpen(post.id) })
                    .semantics { contentDescription = description }
                    .testTag("tile_${post.id}"),
            ) {
                OfflinePhoto(playlist.cover, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f)))))
                Column(
                    Modifier.align(Alignment.BottomStart).padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        playlist.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Avatar(post.author, 20.dp)
                        Text(
                            stringResource(
                                R.string.tile_facts, post.author.name,
                                pluralStringResource(R.plurals.track_count, playlist.tracks.size, playlist.tracks.size),
                            ),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.9f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}
