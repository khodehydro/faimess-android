package com.khodehydro.faimess.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.khodehydro.faimess.R
import com.khodehydro.faimess.model.Track
import com.khodehydro.faimess.model.formatClock
import com.khodehydro.faimess.model.formatLength
import com.khodehydro.faimess.ui.PlayerUiState
import com.khodehydro.faimess.ui.RepeatMode
import com.khodehydro.faimess.ui.components.AppIcon
import com.khodehydro.faimess.ui.components.OfflinePhoto
import com.khodehydro.faimess.ui.components.TrackRow
import com.khodehydro.faimess.ui.theme.CanvasBackground
import com.khodehydro.faimess.ui.theme.Flame
import com.khodehydro.faimess.ui.theme.InkFaint
import com.khodehydro.faimess.ui.theme.InkMuted
import com.khodehydro.faimess.ui.theme.Line
import com.khodehydro.faimess.ui.theme.Primary
import com.khodehydro.faimess.ui.theme.PrimaryDeep
import com.khodehydro.faimess.ui.theme.PrimarySoft

/**
 * Full-screen player matching the FAIMESS web app's right-column player card.
 *
 * Layout (top to bottom):
 *  - Gradient header: close button, play count badge, queue counter
 *  - Large cover art (rounded square)
 *  - Track info: title · artist · album
 *  - Seek bar with timestamps
 *  - Transport controls: shuffle · previous · play/pause · next · repeat
 *  - Action row: favourite · share · queue
 *  - "Up Next" queue list of the current playlist's tracks
 */
@Composable
fun PlayerScreen(
    state: PlayerUiState,
    onBack: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Float) -> Unit,
    onShuffle: () -> Unit,
    onRepeat: () -> Unit,
    onFavourite: () -> Unit,
    onPlayTrack: (Track) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)

    val track = state.track ?: return
    var sliderPosition by remember { mutableStateOf<Float?>(null) }

    // Full-screen gradient background
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        PrimaryDeep,
                        Primary,
                        Primary.copy(alpha = 0.9f),
                        CanvasBackground,
                    )
                )
            )
            .pointerInput(Unit) { detectTapGestures { } }
            .testTag("player_screen"),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 32.dp),
        ) {
            // ---- Header ----
            item(key = "header", contentType = "header") {
                PlayerHeader(
                    track = track,
                    queuePosition = state.queueIndex + 1,
                    queueSize = state.playlist.size,
                    onClose = onBack,
                )
            }

            // ---- Cover Art ----
            item(key = "cover", contentType = "cover") {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 40.dp)
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .shadow(24.dp, RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp))
                        .testTag("player_cover"),
                ) {
                    OfflinePhoto(track.cover, null, Modifier.fillMaxSize())
                }
                Spacer(Modifier.height(28.dp))
            }

            // ---- Track Info ----
            item(key = "info", contentType = "info") {
                Column(
                    modifier = Modifier.padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        track.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "${track.artist} · ${track.album}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(Modifier.height(24.dp))
            }

            // ---- Seek Bar ----
            item(key = "seek", contentType = "seek") {
                Column(modifier = Modifier.padding(horizontal = 28.dp)) {
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
                            thumbColor = Color.White,
                            activeTrackColor = Color.White,
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f),
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
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f),
                        )
                        Text(
                            formatClock((state.durationMs / 1000).toInt()),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f),
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // ---- Transport Controls ----
            item(key = "transport", contentType = "transport") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Shuffle
                    TransportButton(
                        icon = R.drawable.ic_shuffle,
                        label = stringResource(R.string.shuffle_playlist),
                        active = state.shuffle,
                        onClick = onShuffle,
                        modifier = Modifier.size(40.dp),
                        activeTint = PrimarySoft,
                        iconSize = 20.dp,
                    )

                    // Previous
                    TransportButton(
                        icon = R.drawable.ic_skip_previous,
                        label = "Previous",
                        onClick = onPrevious,
                        modifier = Modifier.size(48.dp),
                        tint = Color.White,
                        iconSize = 26.dp,
                    )

                    // Play / Pause — large centred button
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .shadow(12.dp, CircleShape, ambientColor = Color.Black.copy(alpha = 0.3f))
                            .clip(CircleShape)
                            .background(Color.White)
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
                            modifier = Modifier.size(30.dp),
                            tint = PrimaryDeep,
                        )
                    }

                    // Next
                    TransportButton(
                        icon = R.drawable.ic_skip_next,
                        label = "Next",
                        onClick = onNext,
                        modifier = Modifier.size(48.dp),
                        tint = Color.White,
                        iconSize = 26.dp,
                    )

                    // Repeat
                    TransportButton(
                        icon = when (state.repeat) {
                            RepeatMode.One -> R.drawable.ic_repeat_one
                            else -> R.drawable.ic_repeat
                        },
                        label = "Repeat",
                        active = state.repeat != RepeatMode.Off,
                        onClick = onRepeat,
                        modifier = Modifier.size(40.dp),
                        activeTint = PrimarySoft,
                        iconSize = 20.dp,
                    )
                }
                Spacer(Modifier.height(20.dp))
            }

            // ---- Action Row (favourite, info) ----
            item(key = "actions", contentType = "actions") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val favColor by animateColorAsState(
                        if (state.isFavourite) Flame else Color.White.copy(alpha = 0.7f),
                        tween(200, easing = LinearEasing),
                        label = "favColor",
                    )
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
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
                            modifier = Modifier.size(22.dp),
                            tint = favColor,
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }

            // ---- Up Next Queue ----
            if (state.playlist.size > 1) {
                item(key = "queue_title", contentType = "section_title") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CanvasBackground)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
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
                    val isCurrent = index == state.queueIndex
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

/** The player header: close (X), play count badge, queue position pill. */
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
            .padding(start = 4.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
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
                Color.White,
            )
        }

        Spacer(Modifier.weight(1f))

        // Queue position pill
        if (queueSize > 1) {
            Box(
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            ) {
                Text(
                    "$queuePosition / $queueSize",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.85f),
                )
            }
        }
    }
}

/**
 * A circular transport button used in the player's control row.
 * When [active], the background is tinted with [activeTint].
 */
@Composable
private fun TransportButton(
    icon: Int,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    active: Boolean = false,
    tint: Color = Color.White.copy(alpha = 0.85f),
    activeTint: Color = PrimarySoft,
    iconSize: androidx.compose.ui.unit.Dp = 22.dp,
) {
    val bg by animateColorAsState(
        if (active) activeTint else Color.Transparent,
        tween(150, easing = LinearEasing),
        label = "transportBg",
    )
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(bg)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            )
            .testTag("transport_${label.lowercase()}"),
        contentAlignment = Alignment.Center,
    ) {
        AppIcon(icon, label, Modifier.size(iconSize), tint)
    }
}