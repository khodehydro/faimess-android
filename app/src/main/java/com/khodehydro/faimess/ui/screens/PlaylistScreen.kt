package com.khodehydro.faimess.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.khodehydro.faimess.R
import com.khodehydro.faimess.model.FeedPost
import com.khodehydro.faimess.model.compactCount
import com.khodehydro.faimess.ui.components.AppIcon
import com.khodehydro.faimess.ui.components.Avatar
import com.khodehydro.faimess.ui.components.PlaylistCover
import com.khodehydro.faimess.ui.components.TrackRow
import com.khodehydro.faimess.ui.components.playlistFacts
import com.khodehydro.faimess.ui.theme.CanvasBackground
import com.khodehydro.faimess.ui.theme.Heart
import com.khodehydro.faimess.ui.theme.Ink
import com.khodehydro.faimess.ui.theme.InkFaint
import com.khodehydro.faimess.ui.theme.InkMuted
import com.khodehydro.faimess.ui.theme.Line
import com.khodehydro.faimess.ui.theme.Primary
import com.khodehydro.faimess.ui.theme.Subtle

/**
 * The playlist page a feed post opens: the site's detail card (cover, name, owner and facts on the
 * start side, the controls opposite) followed by every song as a track row. It covers the feed
 * and the floating menu; the back button and the system Back gesture return to the feed.
 */
@Composable
fun PlaylistScreen(
    post: FeedPost,
    onBack: () -> Unit,
    onLike: (String) -> Unit,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)
    val playlist = post.playlist
    LazyColumn(
        // Consume stray taps so nothing reaches the feed underneath this page.
        modifier = modifier.fillMaxSize().background(CanvasBackground)
            .pointerInput(Unit) { detectTapGestures { } }
            .navigationBarsPadding()
            .testTag("playlist_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        item(key = "header", contentType = "header") {
            PlaylistHeader(post, onBack, onLike, onPlay)
            Spacer(Modifier.height(8.dp))
        }
        itemsIndexed(playlist.tracks, key = { index, track -> "${track.id}-$index" }, contentType = { _, _ -> "track" }) { index, track ->
            TrackRow(index + 1, track, Modifier.testTag("track_${track.id}"), onClick = onPlay)
        }
    }
}

@Composable
private fun PlaylistHeader(post: FeedPost, onBack: () -> Unit, onLike: (String) -> Unit, onPlay: () -> Unit) {
    val playlist = post.playlist
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(48.dp).padding(4.dp)
                .shadow(8.dp, CircleShape, ambientColor = Ink.copy(alpha = 0.1f), spotColor = Ink.copy(alpha = 0.16f))
                .background(Color.White, CircleShape)
                .testTag("playlist_back"),
        ) {
            AppIcon(R.drawable.ic_arrow_back, stringResource(R.string.back), Modifier.size(20.dp), Ink)
        }
        Surface(shape = RoundedCornerShape(27.dp), color = Color.White) {
            Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    PlaylistCover(playlist, 76.dp, radius = 18.dp)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            playlist.name,
                            style = MaterialTheme.typography.headlineSmall,
                            color = Ink,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Avatar(post.author, 18.dp)
                            Text(
                                stringResource(R.string.playlist_by, post.author.name),
                                style = MaterialTheme.typography.bodyMedium,
                                color = InkMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        Text(playlistFacts(playlist), style = MaterialTheme.typography.bodySmall, color = InkFaint)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onPlay,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = Color.White),
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        modifier = Modifier.weight(1f).heightIn(min = 44.dp).testTag("playlist_play"),
                    ) {
                        AppIcon(R.drawable.ic_play, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.play_playlist), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    OutlinedButton(
                        onClick = onPlay,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Ink),
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        modifier = Modifier.weight(1f).heightIn(min = 44.dp),
                    ) {
                        AppIcon(R.drawable.ic_shuffle, modifier = Modifier.size(16.dp), tint = Ink)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.shuffle_playlist), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    val likeDescription = stringResource(
                        if (post.isLiked) R.string.unlike_post else R.string.like_post, post.author.name,
                    )
                    val likeState = stringResource(R.string.likes_state, post.likes)
                    // The same like as the feed card: one tap here is reflected there and vice versa.
                    Row(
                        modifier = Modifier.heightIn(min = 44.dp).clip(RoundedCornerShape(50)).background(Subtle)
                            .clickable(role = Role.Button) { onLike(post.id) }
                            .semantics { contentDescription = likeDescription; stateDescription = likeState }
                            .testTag("playlist_like")
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        AppIcon(
                            if (post.isLiked) R.drawable.ic_heart else R.drawable.ic_heart_outline,
                            modifier = Modifier.size(18.dp),
                            tint = if (post.isLiked) Heart else Ink,
                        )
                        Text(compactCount(post.likes), style = MaterialTheme.typography.labelMedium, color = Ink)
                    }
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(start = 4.dp)) {
            Text(stringResource(R.string.tracks_section), style = MaterialTheme.typography.labelMedium, color = InkFaint)
            Box(Modifier.background(Line, RoundedCornerShape(50)).padding(horizontal = 8.dp, vertical = 1.dp)) {
                Text(playlist.tracks.size.toString(), style = MaterialTheme.typography.labelMedium, color = InkMuted)
            }
        }
    }
}
