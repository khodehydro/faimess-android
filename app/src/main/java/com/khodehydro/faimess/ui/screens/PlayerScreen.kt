package com.khodehydro.faimess.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.khodehydro.faimess.R
import com.khodehydro.faimess.data.DemoContent
import com.khodehydro.faimess.data.DemoLyrics
import com.khodehydro.faimess.model.LyricLine
import com.khodehydro.faimess.model.PlayerLogic
import com.khodehydro.faimess.model.PlayerState
import com.khodehydro.faimess.model.RepeatMode
import com.khodehydro.faimess.model.compactCount
import com.khodehydro.faimess.model.formatClock
import com.khodehydro.faimess.ui.components.AppIcon
import com.khodehydro.faimess.ui.components.EmptyState
import com.khodehydro.faimess.ui.components.OfflinePhoto
import com.khodehydro.faimess.ui.components.TrackRow
import com.khodehydro.faimess.ui.theme.FaimessTheme
import com.khodehydro.faimess.ui.theme.Heart
import com.khodehydro.faimess.ui.theme.Ink
import com.khodehydro.faimess.ui.theme.InkBody
import com.khodehydro.faimess.ui.theme.InkFaint
import com.khodehydro.faimess.ui.theme.InkMuted
import com.khodehydro.faimess.ui.theme.Line
import com.khodehydro.faimess.ui.theme.Primary
import com.khodehydro.faimess.ui.theme.PrimaryDeep
import com.khodehydro.faimess.ui.theme.PrimaryFaint
import com.khodehydro.faimess.ui.theme.PrimarySoft
import com.khodehydro.faimess.ui.theme.Subtle
import kotlin.math.roundToInt

/** Everything the player page can do; one stable object so the page recomposes only on state. */
@Immutable
data class PlayerActions(
    val onToggle: () -> Unit,
    val onNext: () -> Unit,
    val onPrevious: () -> Unit,
    /** a fraction of the song, 0..1, when the listener lets go of the seek knob. */
    val onSeek: (Float) -> Unit,
    val onLike: () -> Unit,
    val onShare: () -> Unit,
    val onRepeat: () -> Unit,
    val onShuffle: () -> Unit,
    /** jump to a song in the queue (the "Up next" rows). */
    val onPlayAt: (Int) -> Unit,
    /** seek to a lyric line's start, in seconds. */
    val onSeekSeconds: (Int) -> Unit,
)

private enum class PlayerPanel { Lyrics, Queue }

/**
 * The full player, opened from the mini bar. It carries what the website's player section
 * offers: plays and queue-position pills, the artwork (dimmed with a pause glyph while paused),
 * title over artist · album, a scrubbable seek bar with elapsed and remaining clocks, like /
 * previous / play / next / share, shuffle and repeat (off → all → one), then the bilingual lyric
 * sheet highlighted by position or the "Up next" queue. Back and the chevron collapse it.
 */
@Composable
fun PlayerScreen(
    state: PlayerState,
    actions: PlayerActions,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val track = state.track ?: return
    BackHandler(onBack = onClose)
    var panel by rememberSaveable { mutableStateOf(PlayerPanel.Lyrics) }
    val lyrics = remember(track.id) { DemoLyrics.forTrack(track) }
    Column(
        modifier = modifier.fillMaxSize().background(Color.White)
            // Consume stray taps so nothing reaches the pages underneath.
            .pointerInput(Unit) { detectTapGestures { } }
            .navigationBarsPadding()
            .testTag("player_screen"),
    ) {
        PlayerHeader(state, onClose)
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Artwork(state, Modifier.padding(top = 8.dp, bottom = 18.dp))
            Text(
                track.title,
                style = MaterialTheme.typography.headlineSmall,
                color = Ink,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp).testTag("player_title"),
            )
            Spacer(Modifier.height(3.dp))
            Text(
                stringResource(R.string.track_subtitle, track.artist, track.album),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = InkMuted,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp),
            )
            SeekBar(state, actions.onSeek, Modifier.padding(horizontal = 20.dp, vertical = 10.dp))
            Transport(state, actions, Modifier.padding(horizontal = 12.dp))
            ModeRow(state, actions, Modifier.padding(top = 10.dp))
            PanelTabs(panel, lyrics != null, { panel = it }, Modifier.padding(horizontal = 20.dp, vertical = 14.dp))
            when (panel) {
                PlayerPanel.Lyrics -> LyricsPanel(lyrics, state.positionMs / 1000, actions.onSeekSeconds)
                PlayerPanel.Queue -> QueuePanel(state, actions.onPlayAt)
            }
            Text(
                stringResource(R.string.demo_audio_note),
                style = MaterialTheme.typography.bodySmall,
                color = InkFaint,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 12.dp),
            )
        }
    }
}

@Composable
private fun PlayerHeader(state: PlayerState, onClose: () -> Unit) {
    val track = state.track ?: return
    Row(
        modifier = Modifier.fillMaxWidth().height(64.dp).padding(start = 12.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val collapse = stringResource(R.string.collapse_player)
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(Subtle)
                .clickable(role = Role.Button, onClick = onClose)
                .semantics { contentDescription = collapse }
                .testTag("player_close"),
            contentAlignment = Alignment.Center,
        ) {
            AppIcon(R.drawable.ic_expand_more, modifier = Modifier.size(24.dp), tint = Ink)
        }
        Spacer(Modifier.weight(1f))
        Pill(R.drawable.ic_play, stringResource(R.string.plays_count, compactCount(track.plays)), iconSize = 10.dp)
        val position = stringResource(R.string.queue_position_description, state.index + 1, state.queue.size)
        Pill(
            R.drawable.ic_queue_music,
            stringResource(R.string.queue_position, state.index + 1, state.queue.size),
            modifier = Modifier.semantics { contentDescription = position }.testTag("player_queue_pill"),
        )
    }
}

@Composable
private fun Pill(icon: Int, text: String, modifier: Modifier = Modifier, iconSize: Dp = 14.dp) {
    Row(
        modifier = modifier.background(PrimaryFaint, RoundedCornerShape(50)).border(1.dp, PrimarySoft, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        AppIcon(icon, modifier = Modifier.size(iconSize), tint = PrimaryDeep)
        Text(text, style = MaterialTheme.typography.labelMedium, color = PrimaryDeep, maxLines = 1)
    }
}

@Composable
private fun Artwork(state: PlayerState, modifier: Modifier = Modifier) {
    val track = state.track ?: return
    val shape = RoundedCornerShape(24.dp)
    Box(modifier.fillMaxWidth().padding(horizontal = 48.dp), contentAlignment = Alignment.Center) {
        Box(
            Modifier.widthIn(max = 280.dp).fillMaxWidth().aspectRatio(1f)
                .shadow(22.dp, shape, ambientColor = Ink.copy(alpha = 0.18f), spotColor = Ink.copy(alpha = 0.26f))
                .clip(shape),
        ) {
            OfflinePhoto(track.cover, null, Modifier.matchParentSize().alpha(if (state.playing) 1f else 0.86f))
            if (!state.playing) {
                Box(Modifier.matchParentSize().background(Ink.copy(alpha = 0.22f)), contentAlignment = Alignment.Center) {
                    Box(Modifier.size(56.dp).background(Color.White, CircleShape), contentAlignment = Alignment.Center) {
                        AppIcon(R.drawable.ic_pause, modifier = Modifier.size(26.dp), tint = PrimaryDeep)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeekBar(state: PlayerState, onSeek: (Float) -> Unit, modifier: Modifier = Modifier) {
    // While the knob is held the bar follows the finger, not the clock; the seek lands on release.
    var dragging by remember { mutableStateOf<Float?>(null) }
    val shown = dragging ?: state.progress
    val shownPositionMs = dragging?.let { (it * state.durationMs).roundToInt() } ?: state.positionMs
    val seekLabel = stringResource(R.string.seek)
    val seekState = stringResource(R.string.seek_state, formatClock(shownPositionMs / 1000), formatClock(state.durationMs / 1000))
    Column(modifier.fillMaxWidth()) {
        Slider(
            value = shown,
            onValueChange = { dragging = it },
            onValueChangeFinished = {
                dragging?.let(onSeek)
                dragging = null
            },
            colors = SliderDefaults.colors(thumbColor = Primary, activeTrackColor = Primary, inactiveTrackColor = PrimarySoft),
            thumb = {
                Box(
                    Modifier.size(14.dp).shadow(4.dp, CircleShape, spotColor = PrimaryDeep.copy(alpha = 0.5f))
                        .background(Primary, CircleShape).border(2.dp, Color.White, CircleShape),
                )
            },
            track = { sliderState ->
                SliderDefaults.Track(
                    sliderState = sliderState,
                    modifier = Modifier.height(6.dp),
                    colors = SliderDefaults.colors(activeTrackColor = Primary, inactiveTrackColor = PrimarySoft),
                    drawStopIndicator = null,
                    thumbTrackGapSize = 0.dp,
                    trackInsideCornerSize = 0.dp,
                )
            },
            modifier = Modifier.fillMaxWidth().height(32.dp)
                .semantics { contentDescription = seekLabel; stateDescription = seekState }
                .testTag("player_seek"),
        )
        Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(formatClock(shownPositionMs / 1000), style = MaterialTheme.typography.labelMedium, color = InkMuted)
            Text(
                "-" + formatClock((state.durationMs - shownPositionMs).coerceAtLeast(0) / 1000),
                style = MaterialTheme.typography.labelMedium,
                color = InkMuted,
            )
        }
    }
}

@Composable
private fun Transport(state: PlayerState, actions: PlayerActions, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        val liked = state.isLiked
        RoundButton(
            icon = if (liked) R.drawable.ic_heart else R.drawable.ic_heart_outline,
            description = stringResource(if (liked) R.string.unlike_track else R.string.like_track),
            onClick = actions.onLike,
            tint = if (liked) Heart else Ink,
            tag = "player_like",
        )
        RoundButton(R.drawable.ic_skip_previous, stringResource(R.string.previous_track), actions.onPrevious, iconSize = 30.dp, tag = "player_prev")
        val toggle = stringResource(if (state.playing) R.string.pause else R.string.play)
        Box(
            modifier = Modifier.size(64.dp)
                .shadow(14.dp, CircleShape, ambientColor = PrimaryDeep.copy(alpha = 0.35f), spotColor = PrimaryDeep.copy(alpha = 0.5f))
                .clip(CircleShape).background(Primary)
                .clickable(role = Role.Button, onClick = actions.onToggle)
                .semantics { contentDescription = toggle }
                .testTag("player_toggle"),
            contentAlignment = Alignment.Center,
        ) {
            AppIcon(
                if (state.playing) R.drawable.ic_pause else R.drawable.ic_play,
                modifier = Modifier.size(if (state.playing) 30.dp else 24.dp).padding(start = if (state.playing) 0.dp else 3.dp),
                tint = Color.White,
            )
        }
        RoundButton(R.drawable.ic_skip_next, stringResource(R.string.next_track), actions.onNext, iconSize = 30.dp, tag = "player_next")
        RoundButton(R.drawable.ic_send, stringResource(R.string.share_track), actions.onShare, iconSize = 20.dp, tag = "player_share")
    }
}

@Composable
private fun RoundButton(
    icon: Int,
    description: String,
    onClick: () -> Unit,
    tag: String,
    iconSize: Dp = 22.dp,
    tint: Color = Ink,
) {
    Box(
        modifier = Modifier.size(48.dp).clip(CircleShape)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description }
            .testTag(tag),
        contentAlignment = Alignment.Center,
    ) {
        AppIcon(icon, modifier = Modifier.size(iconSize), tint = tint)
    }
}

/** Shuffle and repeat as the site's small pills; the active mode is painted purple. */
@Composable
private fun ModeRow(state: PlayerState, actions: PlayerActions, modifier: Modifier = Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        ModePill(
            icon = R.drawable.ic_shuffle,
            label = stringResource(if (state.shuffle) R.string.shuffle_on else R.string.shuffle_off),
            active = state.shuffle,
            onClick = actions.onShuffle,
            tag = "player_shuffle",
        )
        ModePill(
            icon = if (state.repeat == RepeatMode.One) R.drawable.ic_repeat_one else R.drawable.ic_repeat,
            label = stringResource(
                when (state.repeat) {
                    RepeatMode.Off -> R.string.repeat_off
                    RepeatMode.All -> R.string.repeat_all
                    RepeatMode.One -> R.string.repeat_one
                },
            ),
            active = state.repeat != RepeatMode.Off,
            onClick = actions.onRepeat,
            tag = "player_repeat",
        )
    }
}

@Composable
private fun ModePill(icon: Int, label: String, active: Boolean, onClick: () -> Unit, tag: String) {
    val shape = RoundedCornerShape(50)
    Row(
        modifier = Modifier.heightIn(min = 36.dp).clip(shape)
            .background(if (active) PrimarySoft else Subtle)
            .border(1.dp, if (active) Primary.copy(alpha = 0.35f) else Line, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = label; stateDescription = label }
            .testTag(tag)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        AppIcon(icon, modifier = Modifier.size(16.dp), tint = if (active) PrimaryDeep else InkBody)
        Text(label, style = MaterialTheme.typography.labelMedium, color = if (active) PrimaryDeep else InkBody, maxLines = 1)
    }
}

@Composable
private fun PanelTabs(selected: PlayerPanel, hasLyrics: Boolean, onSelect: (PlayerPanel) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(50)).background(Subtle).border(1.dp, Line, RoundedCornerShape(50))
            .padding(4.dp).selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        PanelTab(R.drawable.ic_lyrics, stringResource(R.string.lyrics_tab), selected == PlayerPanel.Lyrics, { onSelect(PlayerPanel.Lyrics) }, "tab_lyrics", Modifier.weight(1f), dimmed = !hasLyrics)
        PanelTab(R.drawable.ic_queue_music, stringResource(R.string.up_next_tab), selected == PlayerPanel.Queue, { onSelect(PlayerPanel.Queue) }, "tab_queue", Modifier.weight(1f))
    }
}

@Composable
private fun PanelTab(
    icon: Int,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    tag: String,
    modifier: Modifier = Modifier,
    dimmed: Boolean = false,
) {
    val color = when {
        selected -> Color.White
        dimmed -> InkFaint
        else -> InkBody
    }
    Row(
        modifier = modifier.height(36.dp).clip(RoundedCornerShape(50))
            .background(if (selected) Primary else Color.Transparent)
            .selectable(selected = selected, role = Role.Tab, onClick = onClick)
            .testTag(tag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        AppIcon(icon, modifier = Modifier.size(16.dp), tint = color)
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelLarge, color = color, maxLines = 1)
    }
}

private val PanelHeight = 300.dp

/**
 * The bilingual sheet: each line's original over its Persian translation, the current line in
 * ink and the rest faded, scrolled into view as the song moves. Tapping a line seeks to it.
 */
@Composable
private fun LyricsPanel(lines: List<LyricLine>?, positionSeconds: Int, onSeekSeconds: (Int) -> Unit) {
    if (lines.isNullOrEmpty()) {
        EmptyState(
            R.drawable.ic_lyrics,
            stringResource(R.string.no_lyrics_title),
            stringResource(R.string.no_lyrics_body),
            Modifier.height(PanelHeight).testTag("lyrics_panel"),
        )
        return
    }
    val active = PlayerLogic.activeLine(lines, positionSeconds)
    val listState = rememberLazyListState()
    LaunchedEffect(active) { listState.animateScrollToItem(active) }
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxWidth().height(PanelHeight).testTag("lyrics_panel"),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 4.dp, bottom = PanelHeight / 2),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        itemsIndexed(lines, key = { index, _ -> index }) { index, line ->
            val current = index == active
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                    .clickable { onSeekSeconds(line.at) }
                    .alpha(if (current) 1f else 0.55f)
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    line.ko,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (current) Ink else InkBody,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    line.fa,
                    style = MaterialTheme.typography.bodyMedium.copy(textDirection = TextDirection.Rtl),
                    color = if (current) PrimaryDeep else InkMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

/** The queue as track rows, the playing song highlighted; tapping a row jumps to it. */
@Composable
private fun QueuePanel(state: PlayerState, onPlayAt: (Int) -> Unit) {
    val listState = rememberLazyListState()
    val hasLabel = state.queueLabel.isNotBlank()
    // Keep the song before the playing one in view too, so the list reads as "where we are".
    LaunchedEffect(state.index, hasLabel) {
        if (state.index >= 0) listState.animateScrollToItem((state.index - 1).coerceAtLeast(0) + if (hasLabel) 1 else 0)
    }
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxWidth().height(PanelHeight).testTag("queue_panel"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (hasLabel) {
            item(key = "label") {
                Text(
                    stringResource(R.string.up_next_from, state.queueLabel),
                    style = MaterialTheme.typography.labelMedium,
                    color = InkMuted,
                    modifier = Modifier.padding(start = 4.dp, bottom = 2.dp),
                )
            }
        }
        itemsIndexed(state.queue, key = { index, track -> "${track.id}-$index" }) { index, track ->
            TrackRow(
                index + 1,
                track,
                Modifier.testTag("queue_${track.id}"),
                nowPlaying = index == state.index,
                onClick = { onPlayAt(index) },
            )
        }
    }
}

@Preview(widthDp = 393, heightDp = 852)
@Composable
private fun PlayerScreenPreview() {
    val queue = DemoContent.posts.first().playlist.tracks
    FaimessTheme {
        PlayerScreen(
            state = PlayerState(queue = queue, index = 1, queueLabel = "Midnight Seoul Drive", playing = true, positionMs = 36_000, durationMs = 160_000),
            actions = PlayerActions({}, {}, {}, {}, {}, {}, {}, {}, {}, {}),
            onClose = {},
        )
    }
}
