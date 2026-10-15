package com.khodehydro.faimess.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.khodehydro.faimess.R
import com.khodehydro.faimess.model.FeedPost
import com.khodehydro.faimess.model.Person
import com.khodehydro.faimess.model.Playlist
import com.khodehydro.faimess.model.compactCount
import com.khodehydro.faimess.ui.theme.Heart
import com.khodehydro.faimess.ui.theme.Ink
import com.khodehydro.faimess.ui.theme.InkMuted
import com.khodehydro.faimess.ui.theme.Primary
import com.khodehydro.faimess.ui.theme.PrimaryDeep
import com.khodehydro.faimess.ui.theme.Subtle

/** Events carry stable IDs, never a potentially stale snapshot of a post's engagement state. */
@Immutable
data class PostInteractions(
    val onLike: (String) -> Unit,
    val onComments: (String) -> Unit,
    val onShare: (String) -> Unit,
    val onOptions: (String) -> Unit,
    val onOpen: (String) -> Unit,
    val onAuthor: (Person) -> Unit,
)

@Immutable
private data class PostClickActions(
    val like: () -> Unit,
    val comments: () -> Unit,
    val share: () -> Unit,
    val options: () -> Unit,
    val open: () -> Unit,
    val author: () -> Unit,
)

@Composable
fun PostCard(post: FeedPost, interactions: PostInteractions, modifier: Modifier = Modifier) {
    val postId = post.id
    val author = post.author
    val clicks = remember(postId, author, interactions) {
        PostClickActions(
            like = { interactions.onLike(postId) },
            comments = { interactions.onComments(postId) },
            share = { interactions.onShare(postId) },
            options = { interactions.onOptions(postId) },
            open = { interactions.onOpen(postId) },
            author = { interactions.onAuthor(author) },
        )
    }
    Surface(
        modifier = modifier.fillMaxWidth().testTag("post_$postId"),
        shape = RoundedCornerShape(27.dp),
        color = Color.White,
    ) {
        Column(Modifier.padding(horizontal = 14.dp).padding(top = 10.dp, bottom = 3.dp)) {
            // Header, caption and playlist only receive the fields they render. A like/comment update
            // can recompose the footer without rebuilding these more expensive, unchanged children.
            PostAuthorHeader(author, post.time, clicks.author, clicks.options)
            Spacer(Modifier.height(9.dp))
            ExpandableCaption(post.caption, postId)
            Spacer(Modifier.height(13.dp))
            PlaylistPanel(postId, post.playlist, clicks.open)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val likeDescription = stringResource(
                    if (post.isLiked) R.string.unlike_post else R.string.like_post, author.name,
                )
                val likeState = stringResource(R.string.likes_state, post.likes)
                EngagementButton(
                    icon = if (post.isLiked) R.drawable.ic_heart else R.drawable.ic_heart_outline,
                    label = compactCount(post.likes),
                    description = likeDescription,
                    iconColor = if (post.isLiked) Heart else Ink,
                    onClick = clicks.like,
                    modifier = Modifier.testTag("like_$postId").semantics { stateDescription = likeState },
                )
                Spacer(Modifier.width(4.dp))
                EngagementButton(
                    icon = R.drawable.ic_comment,
                    label = compactCount(post.commentCount),
                    description = stringResource(R.string.post_comments, author.name),
                    onClick = clicks.comments,
                )
                Spacer(Modifier.weight(1f))
                EngagementButton(
                    icon = R.drawable.ic_send,
                    label = compactCount(post.shares),
                    description = stringResource(R.string.share_post, author.name),
                    onClick = clicks.share,
                    iconAfterText = true,
                )
            }
        }
    }
}

@Composable
private fun PostAuthorHeader(author: Person, time: String, onAuthor: () -> Unit, onOptions: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier.weight(1f).heightIn(min = 48.dp).clickable(role = Role.Button, onClick = onAuthor),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Avatar(author, 36.dp)
            Spacer(Modifier.width(9.dp))
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(author.name, style = MaterialTheme.typography.titleSmall, color = Ink)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(time, style = MaterialTheme.typography.bodySmall, color = InkMuted)
                    AppIcon(R.drawable.ic_globe, stringResource(R.string.public_post), Modifier.size(10.dp), Ink)
                }
            }
        }
        IconButton(onClick = onOptions, modifier = Modifier.size(48.dp)) {
            AppIcon(R.drawable.ic_more, stringResource(R.string.post_options, author.name), Modifier.size(16.dp), InkMuted)
        }
    }
}

/** The post's body: the shared playlist's name, facts and first songs. Tapping it opens the playlist. */
@Composable
private fun PlaylistPanel(postId: String, playlist: Playlist, onClick: () -> Unit) {
    val description = stringResource(R.string.open_playlist, playlist.name)
    val facts = playlistFacts(playlist)
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(21.dp)).background(Subtle)
            .clickable(role = Role.Button, onClick = onClick).semantics { contentDescription = description }
            .testTag("playlist_$postId").padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 2.dp, end = 2.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PlaylistCover(playlist, 56.dp)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(playlist.name, style = MaterialTheme.typography.titleMedium, color = Ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(facts, style = MaterialTheme.typography.bodySmall, color = InkMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            AppIcon(R.drawable.ic_play_circle, modifier = Modifier.size(36.dp), tint = Primary)
        }
        playlist.tracks.take(PREVIEW_TRACKS).forEachIndexed { index, track ->
            TrackRow(index + 1, track, compact = true)
        }
        val hidden = playlist.tracks.size - PREVIEW_TRACKS
        if (hidden > 0) {
            Text(
                pluralStringResource(R.plurals.more_tracks, hidden, hidden),
                style = MaterialTheme.typography.labelMedium,
                color = PrimaryDeep,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp, bottom = 2.dp),
            )
        }
    }
}

private const val PREVIEW_TRACKS = 4

@Composable
private fun EngagementButton(
    @DrawableRes icon: Int,
    label: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconColor: Color = Ink,
    iconAfterText: Boolean = false,
) {
    // Retain standard accessible click/ripple and drag cancellation, with a 48dp hit target.
    Box(
        modifier = modifier.heightIn(min = 48.dp).clip(RoundedCornerShape(24.dp))
            .clickable(role = Role.Button, onClick = onClick).semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.background(Subtle.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
                .padding(horizontal = 9.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            if (!iconAfterText) AppIcon(icon, modifier = Modifier.size(17.dp), tint = iconColor)
            Text(label, style = MaterialTheme.typography.labelMedium, color = Ink)
            if (iconAfterText) AppIcon(icon, modifier = Modifier.size(17.dp), tint = iconColor)
        }
    }
}

@Composable
private fun ExpandableCaption(caption: String, postId: String) {
    var expanded by rememberSaveable(postId) { mutableStateOf(false) }
    val style = MaterialTheme.typography.bodyMedium.copy(color = Ink)
    val more = stringResource(R.string.more_caption)
    val less = stringResource(R.string.less_caption)
    val measurer = rememberTextMeasurer()
    BoxWithConstraints(
        Modifier.fillMaxWidth().animateContentSize(tween(durationMillis = 120, easing = FastOutSlowInEasing)),
    ) {
        val width = constraints.maxWidth
        val collapsed = remember(caption, width, style, more, measurer) {
            fun fits(text: AnnotatedString) = !measurer.measure(
                text = text, style = style, constraints = Constraints(maxWidth = width), maxLines = 2,
            ).hasVisualOverflow
            if (fits(AnnotatedString(caption))) {
                AnnotatedString(caption)
            } else {
                fun candidate(length: Int) = buildAnnotatedString {
                    append(caption.take(length).trimEnd())
                    append(" … ")
                    withStyle(SpanStyle(color = PrimaryDeep, fontWeight = FontWeight.SemiBold)) { append(more) }
                }
                var low = 0
                var high = caption.length
                while (low < high) {
                    val mid = (low + high + 1) / 2
                    if (fits(candidate(mid))) low = mid else high = mid - 1
                }
                candidate(low)
            }
        }
        val canExpand = collapsed.text != caption
        val displayed = if (!expanded) collapsed else buildAnnotatedString {
            append(caption)
            append("  ")
            withStyle(SpanStyle(color = PrimaryDeep, fontWeight = FontWeight.SemiBold)) { append(less) }
        }
        Text(
            text = displayed,
            style = style,
            modifier = if (canExpand) Modifier.clickable(role = Role.Button) { expanded = !expanded }
                .testTag("caption_$postId") else Modifier,
        )
    }
}
