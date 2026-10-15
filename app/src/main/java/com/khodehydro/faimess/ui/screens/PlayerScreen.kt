package com.khodehydro.faimess.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.khodehydro.faimess.R
import com.khodehydro.faimess.model.LyricLine
import com.khodehydro.faimess.model.Track
import com.khodehydro.faimess.model.formatClock
import com.khodehydro.faimess.model.formatLength
import com.khodehydro.faimess.ui.PlayerUiState
import com.khodehydro.faimess.ui.RepeatMode
import com.khodehydro.faimess.ui.components.AppIcon
import com.khodehydro.faimess.ui.components.OfflinePhoto
import com.khodehydro.faimess.ui.components.TrackRow
import com.khodehydro.faimess.ui.theme.CanvasBackground
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

/**
 * Full-screen player — a pixel-level copy of the FAIMESS web app's player section
 * (src/sections/PlayerSection.tsx).
 *
 * White background.  Layout top to bottom:
 *  - Header: close (X), queue position pill (6/9)
 *  - Cover art (rounded square, centered, animates scale with playback)
 *  - Title · artist · album
 *  - Seek bar (purple track, current time | remaining time)
 *  - Transport: Like · Add-to-playlist | Prev · Play/Pause · Next | Share · Repeat
 *  - Lyrics panel (bilingual Korean + Persian, active line highlighted)
 *  - Up Next queue
 */
@Composable
fun PlayerScreen(
    state: PlayerUiState,
    onBack: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Float) -> Unit,
    onSeekSeconds: (Int) -> Unit,
    onShuffle: () -> Unit,
    onRepeat: () -> Unit,
    onFavourite: () -> Unit,
    onPlayTrack: (Track) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)

    val track = state.track ?: return
    var sliderPosition by remember { mutableStateOf<Float?>(null) }

    // Cover scale: shrinks slightly when paused (matches the site's spring animation)
    val coverScale by animateFloatAsState(
        targetValue = if (state.playing) 1f else 0.97f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
        label = "coverScale",
    )

    // Auto-scroll lyrics to active line
    val lyricsListState = rememberLazyListState()
    LaunchedEffect(state.activeLyricIndex) {
        if (state.activeLyricIndex >= 0 && state.playing) {
            lyricsListState.animateScrollToItem(
                index = state.activeLyricIndex.coerceAtLeast(0),
                scrollOffset = -200,
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBackground)
            .pointerInput(Unit) { detectTapGestures { } }
            .testTag("player_screen"),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            // ── Header: close + queue position ──
            PlayerHeader(
                track = track,
                queuePosition = state.queueIndex + 1,
                queueSize = state.playlist.size,
                onClose = onBack,
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(bottom = 32.dp),
            ) {
                // ── Cover Art ──
                item(key = "cover", contentType = "cover") {
                    Box(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .size((118 * coverScale).dp)
                            .shadow(16.dp, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .testTag("player_cover"),
                    ) {
                        OfflinePhoto(track.cover, null, Modifier.fillMaxSize())
                        // Dim overlay when paused
                        if (!state.playing) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.35f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                AppIcon(
                                    R.drawable.ic_pause,
                                    modifier = Modifier.size(22.dp),
                                    tint = Color.White,
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }

                // ── Title + Artist · Album ──
                item(key = "info", contentType = "info") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            track.title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.01).sp,
                            ),
                            color = Ink,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "${track.artist}  ·  ${track.album}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = InkMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Spacer(Modifier.height(20.dp))
                }

                // ── Seek Bar ──
                item(key = "seek", contentType = "seek") {
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        Slider(
                            value = sliderPosition ?: state.progress,
                            onValueChange = { sliderPosition = it },
                            onValueChangeFinished = {
                                sliderPosition?.let(onSeek)
                                sliderPosition = null
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("player_seek"),
                            colors = SliderDefaults.colors(
                                thumbColor = Primary,
                                activeTrackColor = Primary,
                                inactiveTrackColor = Subtle,
                            ),
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                formatClock(((sliderPosition ?: state.progress) * state.durationMs / 1000).toInt()),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = InkFaint,
                            )
                            Text(
                                "-${formatClock(((1f - (sliderPosition ?: state.progress)) * state.durationMs / 1000).toInt().coerceAtLeast(0))}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = InkFaint,
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }

                // ── Transport Controls ──
                // Like · AddToPlaylist | Prev · Play/Pause · Next | Share · Repeat
                item(key = "transport", contentType = "transport") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Left cluster: Like
                        val favColor by animateColorAsState(
                            if (state.isFavourite) PrimaryDeep else InkMuted,
                            tween(150, easing = LinearEasing),
                            label = "favColor",
                        )
                        val favBg by animateColorAsState(
                            if (state.isFavourite) PrimarySoft else Color.Transparent,
                            tween(150, easing = LinearEasing),
                            label = "favBg",
                        )
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(favBg)
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() },
                                    onClick = onFavourite,
                                )
                                .testTag("player_favourite"),
                            contentAlignment = Alignment.Center,
                        ) {
                            AppIcon(
                                if (state.isFavourite) R.drawable.ic_heart else R.drawable.ic_heart_outline,
                                modifier = Modifier.size(17.dp),
                                tint = favColor,
                            )
                        }

                        Spacer(Modifier.weight(1f))

                        // Center cluster: Prev · Play/Pause · Next
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            // Previous
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() },
                                        onClick = onPrevious,
                                    )
                                    .testTag("player_prev"),
                                contentAlignment = Alignment.Center,
                            ) {
                                AppIcon(
                                    R.drawable.ic_skip_previous,
                                    modifier = Modifier.size(17.dp),
                                    tint = InkBody,
                                )
                            }

                            // Play / Pause — 44dp primary circle
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .shadow(8.dp, CircleShape, ambientColor = Primary.copy(alpha = 0.3f))
                                    .clip(CircleShape)
                                    .background(Primary)
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() },
                                        onClick = onTogglePlayPause,
                                    )
                                    .testTag("player_play_pause"),
                                contentAlignment = Alignment.Center,
                            ) {
                                AppIcon(
                                    if (state.playing) R.drawable.ic_pause else R.drawable.ic_play,
                                    modifier = Modifier.size(19.dp),
                                    tint = Color.White,
                                )
                            }

                            // Next
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() },
                                        onClick = onNext,
                                    )
                                    .testTag("player_next"),
                                contentAlignment = Alignment.Center,
                            ) {
                                AppIcon(
                                    R.drawable.ic_skip_next,
                                    modifier = Modifier.size(17.dp),
                                    tint = InkBody,
                                )
                            }
                        }

                        Spacer(Modifier.weight(1f))

                        // Right cluster: Repeat
                        val repeatActive = state.repeat != RepeatMode.Off
                        val repeatColor by animateColorAsState(
                            if (repeatActive) PrimaryDeep else InkMuted,
                            tween(150, easing = LinearEasing),
                            label = "repeatColor",
                        )
                        val repeatBg by animateColorAsState(
                            if (repeatActive) PrimarySoft else Color.Transparent,
                            tween(150, easing = LinearEasing),
                            label = "repeatBg",
                        )
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(repeatBg)
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() },
                                    onClick = onRepeat,
                                )
                                .testTag("player_repeat"),
                            contentAlignment = Alignment.Center,
                        ) {
                            AppIcon(
                                when (state.repeat) {
                                    RepeatMode.One -> R.drawable.ic_repeat_one
                                    else -> R.drawable.ic_repeat
                                },
                                modifier = Modifier.size(17.dp),
                                tint = repeatColor,
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }

                // ── Lyrics Panel ──
                item(key = "lyrics_panel", contentType = "lyrics") {
                    val lines = state.lyrics
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(top = 1.dp),
                    ) {
                        // Section header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                "LYRICS",
                                style = MaterialTheme.typography.labelMedium,
                                color = InkFaint,
                            )
                            if (lines != null) {
                                Box(
                                    Modifier
                                        .background(Line, RoundedCornerShape(50))
                                        .padding(horizontal = 8.dp, vertical = 1.dp),
                                ) {
                                    Text(
                                        "${lines.size}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = InkMuted,
                                    )
                                }
                            }
                        }

                        if (lines == null) {
                            // Empty state: ask to send lyrics
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Box(
                                    Modifier
                                        .size(36.dp)
                                        .background(PrimarySoft, CircleShape),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    AppIcon(
                                        R.drawable.ic_comment,
                                        modifier = Modifier.size(16.dp),
                                        tint = PrimaryDeep,
                                    )
                                }
                                Text(
                                    stringResource(R.string.lyrics_empty_title),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Ink,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    stringResource(R.string.lyrics_empty_body),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = InkMuted,
                                )
                            }
                        } else {
                            // Show lyrics
                            LazyColumn(
                                state = lyricsListState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 400.dp)
                                    .padding(horizontal = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                                contentPadding = PaddingValues(bottom = 16.dp),
                            ) {
                                itemsIndexed(
                                    lines,
                                    key = { i, line -> "${line.at}-$i" },
                                    contentType = { _, _ -> "lyric_line" },
                                ) { index, line ->
                                    val isActive = index == state.activeLyricIndex
                                    LyricLineRow(
                                        line = line,
                                        isActive = isActive,
                                    )
                                }
                            }
                        }
                    }
                }

                // ── Up Next Queue ──
                if (state.playlist.size > 1) {
                    item(key = "queue_title", contentType = "section_title") {
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                stringResource(R.string.tracks_section),
                                style = MaterialTheme.typography.labelMedium,
                                color = InkFaint,
                            )
                            Box(
                                Modifier
                                    .background(Line, RoundedCornerShape(50))
                                    .padding(horizontal = 8.dp, vertical = 1.dp),
                            ) {
                                Text(
                                    state.playlist.size.toString(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = InkMuted,
                                )
                            }
                            Spacer(Modifier.weight(1f))
                            Text(
                                formatLength(state.playlist.sumOf { it.seconds }),
                                style = MaterialTheme.typography.labelMedium,
                                color = InkFaint,
                            )
                        }
                    }

                    itemsIndexed(
                        state.playlist,
                        key = { _, t -> "queue_${t.id}" },
                        contentType = { _, _ -> "queue_track" },
                    ) { index, trackItem ->
                        TrackRow(
                            index = index + 1,
                            track = trackItem,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .testTag("queue_track_${trackItem.id}"),
                            onClick = { onPlayTrack(trackItem) },
                        )
                    }
                }
            }
        }
    }
}

/** The player header: close (X) on the left, queue position pill on the right. */
@Composable
private fun PlayerHeader(
    track: Track,
    queuePosition: Int,
    queueSize: Int,
    onClose: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CanvasBackground)
            .padding(start = 4.dp, end = 16.dp, top = 4.dp, bottom = 0.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Close button
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onClose,
                )
                .testTag("player_close"),
            contentAlignment = Alignment.Center,
        ) {
            AppIcon(
                R.drawable.ic_close,
                stringResource(R.string.close),
                Modifier.size(22.dp),
                Ink,
            )
        }

        Spacer(Modifier.weight(1f))

        // Queue position pill
        if (queueSize > 1) {
            Row(
                modifier = Modifier
                    .background(Subtle, RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AppIcon(
                    R.drawable.ic_queue_music,
                    modifier = Modifier.size(12.dp),
                    tint = InkMuted,
                )
                Text(
                    "$queuePosition / $queueSize",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = InkMuted,
                )
            }
        }
    }
}

/** One lyric line — bilingual, highlighted when active. */
@Composable
private fun LyricLineRow(
    line: LyricLine,
    isActive: Boolean,
) {
    val bg by animateColorAsState(
        if (isActive) PrimaryFaint else Color.Transparent,
        tween(200, easing = LinearEasing),
        label = "lyricBg",
    )
    val koColor by animateColorAsState(
        if (isActive) Ink else InkMuted.copy(alpha = 0.6f),
        tween(300, easing = LinearEasing),
        label = "lyricKo",
    )
    val faColor by animateColorAsState(
        if (isActive) PrimaryDeep else InkFaint,
        tween(300, easing = LinearEasing),
        label = "lyricFa",
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Text(
            line.ko,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                lineHeight = 20.sp,
            ),
            color = koColor,
        )
        Text(
            line.fa,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                lineHeight = 18.sp,
            ),
            color = faColor,
        )
    }
}