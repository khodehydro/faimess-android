package com.khodehydro.faimess.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.khodehydro.faimess.R
import com.khodehydro.faimess.model.FeedPost
import com.khodehydro.faimess.model.Playlist
import com.khodehydro.faimess.model.Track
import com.khodehydro.faimess.model.compactCount
import com.khodehydro.faimess.model.formatLength
import com.khodehydro.faimess.ui.components.AppIcon
import com.khodehydro.faimess.ui.components.Avatar
import com.khodehydro.faimess.ui.components.LocalBottomChromeHeight
import com.khodehydro.faimess.ui.components.PlaylistCover
import com.khodehydro.faimess.ui.components.PostAuthorHeader
import com.khodehydro.faimess.ui.components.PostEngagementRow
import com.khodehydro.faimess.ui.components.PostInteractions
import com.khodehydro.faimess.ui.components.TrackRow
import com.khodehydro.faimess.ui.theme.CanvasBackground
import com.khodehydro.faimess.ui.theme.Flame
import com.khodehydro.faimess.ui.theme.FlameSoft
import com.khodehydro.faimess.ui.theme.Ink
import com.khodehydro.faimess.ui.theme.InkFaint
import com.khodehydro.faimess.ui.theme.InkMuted
import com.khodehydro.faimess.ui.theme.Line
import com.khodehydro.faimess.ui.theme.Mint
import com.khodehydro.faimess.ui.theme.MintSoft
import com.khodehydro.faimess.ui.theme.Primary
import com.khodehydro.faimess.ui.theme.PrimaryDeep
import com.khodehydro.faimess.ui.theme.PrimarySoft
import com.khodehydro.faimess.ui.theme.Subtle
import com.khodehydro.faimess.ui.theme.TealDeep
import com.khodehydro.faimess.ui.theme.TealSoft

/** What the playlist page can start: the whole list, a shuffled list, or one song inside it. */
@Immutable
data class PlaylistPlayback(
    val onPlayAll: (Playlist) -> Unit,
    val onShuffle: (Playlist) -> Unit,
    val onTrack: (Playlist, Track) -> Unit,
)

/**
 * The page a playlist post opens. It is built like the home screen: the same 70 dp white header,
 * with the playlist's circular cover where the logo is, its name where the wordmark is, and share
 * and play where create and notifications are. Below it, a card with the owner, caption, facts and
 * controls, every song as a track row, the artists on the playlist, more playlists from the feed
 * and the comments. The floating menu stays visible; Back and the header arrow return to the feed.
 */
@Composable
fun PlaylistScreen(
    post: FeedPost,
    suggestions: List<FeedPost>,
    interactions: PostInteractions,
    playback: PlaylistPlayback,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    nowPlayingId: String? = null,
) {
    BackHandler(onBack = onBack)
    val playlist = post.playlist
    val onPlayAll = remember(playback, playlist) { { playback.onPlayAll(playlist) } }
    val onShuffle = remember(playback, playlist) { { playback.onShuffle(playlist) } }
    Column(
        // Consume stray taps so nothing reaches the feed underneath this page.
        modifier = modifier.fillMaxSize().background(CanvasBackground)
            .pointerInput(Unit) { detectTapGestures { } }
            .testTag("playlist_screen"),
    ) {
        PlaylistHeader(playlist, onBack, onShare = { interactions.onShare(post.id) }, onPlay = onPlayAll)
        LazyColumn(
            modifier = Modifier.fillMaxSize().testTag("playlist_list"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = LocalBottomChromeHeight.current),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item(key = "about", contentType = "about") {
                AboutCard(post, interactions, onPlayAll, onShuffle)
                Spacer(Modifier.height(6.dp))
            }
            item(key = "tracks_title", contentType = "title") {
                SectionTitle(
                    stringResource(R.string.tracks_section),
                    badge = playlist.tracks.size.toString(),
                    trailing = formatLength(playlist.totalSeconds),
                )
            }
            itemsIndexed(playlist.tracks, key = { index, track -> "${track.id}-$index" }, contentType = { _, _ -> "track" }) { index, track ->
                TrackRow(
                    index + 1,
                    track,
                    Modifier.testTag("track_${track.id}"),
                    nowPlaying = track.id == nowPlayingId,
                    onClick = { playback.onTrack(playlist, track) },
                )
            }
            item(key = "artists", contentType = "artists") {
                Spacer(Modifier.height(6.dp))
                ArtistsSection(playlist)
            }
            if (suggestions.isNotEmpty()) {
                item(key = "more", contentType = "more") {
                    Spacer(Modifier.height(6.dp))
                    MorePlaylists(suggestions, interactions.onOpen)
                }
            }
            item(key = "comments", contentType = "comments") {
                Spacer(Modifier.height(6.dp))
                CommentsCard(post, onComments = { interactions.onComments(post.id) })
            }
        }
    }
}

/** The home header's twin: cover for logo, name for wordmark, share and play for create and bell. */
@Composable
private fun PlaylistHeader(playlist: Playlist, onBack: () -> Unit, onShare: () -> Unit, onPlay: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color.White).height(70.dp)
            .padding(start = 4.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack, modifier = Modifier.size(44.dp).testTag("playlist_back")) {
            AppIcon(R.drawable.ic_arrow_back, stringResource(R.string.back), Modifier.size(22.dp), Ink)
        }
        PlaylistCover(playlist, 34.dp, shape = CircleShape)
        Spacer(Modifier.width(9.dp))
        FittedTitle(playlist.name, Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            IconButton(onClick = onShare, modifier = Modifier.size(48.dp).testTag("playlist_share")) {
                Box(Modifier.size(40.dp).background(Subtle, CircleShape), contentAlignment = Alignment.Center) {
                    AppIcon(R.drawable.ic_send, stringResource(R.string.share_playlist), Modifier.size(18.dp), Ink)
                }
            }
            IconButton(onClick = onPlay, modifier = Modifier.size(48.dp).testTag("playlist_play")) {
                Box(Modifier.size(40.dp).background(Primary, CircleShape), contentAlignment = Alignment.Center) {
                    AppIcon(R.drawable.ic_play, stringResource(R.string.play_playlist), Modifier.size(17.dp), Color.White)
                }
            }
        }
    }
}

/**
 * The playlist's name in the wordmark's clothes (extra-bold, -0.02 em). Long names are measured
 * and shrunk from 22 sp down to 15 sp before they ellipsize, so one line still reads as a title.
 */
@Composable
private fun FittedTitle(text: String, modifier: Modifier = Modifier) {
    val base = MaterialTheme.typography.headlineSmall.copy(
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.02).em,
        color = Ink,
    )
    val measurer = rememberTextMeasurer()
    BoxWithConstraints(modifier) {
        val width = constraints.maxWidth
        val fontSize = remember(text, width, base, measurer) {
            var size = MAX_TITLE_SP
            while (size > MIN_TITLE_SP) {
                val layout = measurer.measure(
                    text = AnnotatedString(text),
                    style = base.copy(fontSize = size.sp),
                    constraints = Constraints(maxWidth = width),
                    maxLines = 1,
                )
                if (!layout.hasVisualOverflow) break
                size -= TITLE_STEP_SP
            }
            size.sp
        }
        Text(
            text,
            style = base.copy(fontSize = fontSize),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.semantics { heading() },
        )
    }
}

private const val MAX_TITLE_SP = 22f
private const val MIN_TITLE_SP = 15f
private const val TITLE_STEP_SP = 0.5f

/** Owner, caption, facts and controls in the feed card's own clothes, so the page continues the feed. */
@Composable
private fun AboutCard(post: FeedPost, interactions: PostInteractions, onPlay: () -> Unit, onShuffle: () -> Unit) {
    val playlist = post.playlist
    Surface(shape = RoundedCornerShape(27.dp), color = Color.White, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(horizontal = 14.dp).padding(top = 10.dp, bottom = 3.dp)) {
            PostAuthorHeader(post.author, post.time, { interactions.onAuthor(post.author) }, { interactions.onOptions(post.id) })
            if (post.caption.isNotBlank()) {
                Spacer(Modifier.height(9.dp))
                Text(post.caption, style = MaterialTheme.typography.bodyMedium, color = Ink)
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                FactChip(pluralStringResource(R.plurals.track_count, playlist.tracks.size, playlist.tracks.size))
                FactChip(formatLength(playlist.totalSeconds))
                FactChip(playlist.mood, background = PrimarySoft, color = PrimaryDeep)
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = onPlay,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    modifier = Modifier.weight(1f).heightIn(min = 44.dp),
                ) {
                    AppIcon(R.drawable.ic_play, modifier = Modifier.size(15.dp), tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.play_playlist), maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                OutlinedButton(
                    onClick = onShuffle,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Ink),
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    modifier = Modifier.weight(1f).heightIn(min = 44.dp),
                ) {
                    AppIcon(R.drawable.ic_shuffle, modifier = Modifier.size(16.dp), tint = Ink)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.shuffle_playlist), maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            Spacer(Modifier.height(4.dp))
            // The very same like, comment and share as the card in the feed.
            PostEngagementRow(
                post,
                onLike = { interactions.onLike(post.id) },
                onComments = { interactions.onComments(post.id) },
                onShare = { interactions.onShare(post.id) },
                likeTag = "playlist_like",
            )
        }
    }
}

@Composable
private fun FactChip(text: String, background: Color = Subtle, color: Color = InkMuted) {
    Text(
        text,
        style = MaterialTheme.typography.labelMedium,
        color = color,
        modifier = Modifier.background(background, RoundedCornerShape(10.dp)).padding(horizontal = 9.dp, vertical = 5.dp),
    )
}

@Composable
private fun SectionTitle(title: String, badge: String? = null, trailing: String? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 4.dp, end = 4.dp, top = 4.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(title, style = MaterialTheme.typography.labelMedium, color = InkFaint, modifier = Modifier.semantics { heading() })
        if (badge != null) {
            Box(Modifier.background(Line, RoundedCornerShape(50)).padding(horizontal = 8.dp, vertical = 1.dp)) {
                Text(badge, style = MaterialTheme.typography.labelMedium, color = InkMuted)
            }
        }
        Spacer(Modifier.weight(1f))
        if (trailing != null) Text(trailing, style = MaterialTheme.typography.labelMedium, color = InkFaint)
    }
}

private data class ArtistSummary(val name: String, val songs: Int)

private val ArtistTones = listOf(
    PrimaryDeep to PrimarySoft,
    TealDeep to TealSoft,
    Flame to FlameSoft,
    Mint to MintSoft,
)

/** Who is on the playlist, in order of first appearance; a lettered disc stands in for artist photos. */
@Composable
private fun ArtistsSection(playlist: Playlist) {
    val artists = remember(playlist) {
        playlist.tracks.groupBy { it.artist }.map { (name, tracks) -> ArtistSummary(name, tracks.size) }
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionTitle(stringResource(R.string.artists_section), badge = artists.size.toString())
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            artists.forEachIndexed { index, artist ->
                val (tone, toneSoft) = ArtistTones[index % ArtistTones.size]
                Row(
                    modifier = Modifier.clip(RoundedCornerShape(50)).background(Color.White)
                        .padding(start = 4.dp, end = 12.dp, top = 4.dp, bottom = 4.dp)
                        .semantics(mergeDescendants = true) {},
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(Modifier.size(32.dp).background(toneSoft, CircleShape), contentAlignment = Alignment.Center) {
                        Text(artist.name.take(1), style = MaterialTheme.typography.labelLarge, color = tone)
                    }
                    Column {
                        Text(artist.name, style = MaterialTheme.typography.titleSmall, color = Ink, maxLines = 1)
                        Text(
                            pluralStringResource(R.plurals.song_count, artist.songs, artist.songs),
                            style = MaterialTheme.typography.bodySmall,
                            color = InkMuted,
                        )
                    }
                }
            }
        }
    }
}

/** Other playlists from the feed; tapping one swaps it into this page. */
@Composable
private fun MorePlaylists(posts: List<FeedPost>, onOpen: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionTitle(stringResource(R.string.more_playlists_section))
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            posts.forEach { post ->
                val description = stringResource(R.string.open_playlist, post.playlist.name)
                Column(
                    modifier = Modifier.width(132.dp).clip(RoundedCornerShape(20.dp)).background(Color.White)
                        .clickable(role = Role.Button) { onOpen(post.id) }
                        .semantics { contentDescription = description }
                        .testTag("suggestion_${post.id}")
                        .padding(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    PlaylistCover(post.playlist, 120.dp, radius = 16.dp)
                    Column(Modifier.padding(horizontal = 4.dp, vertical = 2.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(post.playlist.name, style = MaterialTheme.typography.titleSmall, color = Ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            Avatar(post.author, 14.dp)
                            Text(
                                stringResource(R.string.playlist_by, post.author.name),
                                style = MaterialTheme.typography.bodySmall,
                                color = InkMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
    }
}

/** The post's comments: the ones added on this device, and the way into the comments sheet. */
@Composable
private fun CommentsCard(post: FeedPost, onComments: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionTitle(stringResource(R.string.comments_section), badge = compactCount(post.commentCount))
        Surface(shape = RoundedCornerShape(22.dp), color = Color.White, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                val recent = post.localComments.takeLast(3)
                if (recent.isEmpty()) {
                    Text(stringResource(R.string.no_comments_description), style = MaterialTheme.typography.bodyMedium, color = InkMuted)
                }
                recent.forEach { comment ->
                    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                        Text(comment.author, style = MaterialTheme.typography.titleSmall, color = Ink)
                        Text(comment.body, style = MaterialTheme.typography.bodyMedium, color = Ink)
                    }
                }
                OutlinedButton(
                    onClick = onComments,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryDeep),
                    modifier = Modifier.fillMaxWidth().heightIn(min = 44.dp).testTag("playlist_comments"),
                ) {
                    AppIcon(R.drawable.ic_comment, modifier = Modifier.size(16.dp), tint = PrimaryDeep)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.add_comment))
                }
            }
        }
    }
}
