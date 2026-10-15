package com.khodehydro.faimess.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.khodehydro.faimess.R
import com.khodehydro.faimess.ui.PlayerUiState
import com.khodehydro.faimess.ui.theme.Ink
import com.khodehydro.faimess.ui.theme.InkMuted
import com.khodehydro.faimess.ui.theme.Line
import com.khodehydro.faimess.ui.theme.Primary
import com.khodehydro.faimess.ui.theme.PrimaryDeep

/**
 * A compact player bar that sits above the floating navigation, matching
 * the width of the full nav (menu capsule + search circle) and using the
 * same translucent-white capsule shell as the nav itself.
 *
 * Layout: cover · title/artist · play/pause · next — with a thin purple
 * progress bar at the bottom.
 */
@Composable
fun MiniPlayer(
    state: PlayerUiState,
    onTogglePlayPause: () -> Unit,
    onNext: () -> Unit,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val track = state.track ?: return

    AnimatedVisibility(
        visible = state.hasTrack,
        enter = slideInVertically(tween(340)) { it } + fadeIn(tween(340)),
        exit = slideOutVertically(tween(280)) { it } + fadeOut(tween(280)),
        modifier = modifier,
    ) {
        // Same translucent-white shell as the floating nav, full capsule shape
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(18.dp, RoundedCornerShape(50), ambientColor = Ink.copy(alpha = 0.16f), spotColor = Ink.copy(alpha = 0.22f))
                .clip(RoundedCornerShape(50))
                .background(Brush.verticalGradient(listOf(Color(0xF2FFFFFF), Color(0xE6FFFFFF))))
                .border(1.dp, Line, RoundedCornerShape(50))
                .testTag("mini_player"),
        ) {
            // The whole bar is clickable → open the full player.
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onOpen,
                    ),
            )

            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 6.dp, end = 6.dp, top = 6.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Cover art
                    OfflinePhoto(
                        track.cover,
                        null,
                        Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(14.dp)),
                    )

                    Spacer(Modifier.width(10.dp))

                    // Title + Artist
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(1.dp),
                    ) {
                        Text(
                            track.title,
                            style = androidx.compose.material3.MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Ink,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            track.artist,
                            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = InkMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    // Play / Pause — purple filled circle
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Primary)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = onTogglePlayPause,
                            )
                            .testTag("mini_play_pause"),
                        contentAlignment = Alignment.Center,
                    ) {
                        AppIcon(
                            if (state.playing) R.drawable.ic_pause else R.drawable.ic_play,
                            modifier = Modifier.size(17.dp),
                            tint = Color.White,
                        )
                    }

                    Spacer(Modifier.width(4.dp))

                    // Next
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = onNext,
                            )
                            .testTag("mini_next"),
                        contentAlignment = Alignment.Center,
                    ) {
                        AppIcon(
                            R.drawable.ic_skip_next,
                            modifier = Modifier.size(17.dp),
                            tint = InkMuted,
                        )
                    }
                }

                // Thin purple progress bar at the bottom
                LinearProgressIndicator(
                    progress = { state.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = Primary,
                    trackColor = Primary.copy(alpha = 0.15f),
                    strokeCap = StrokeCap.Round,
                )
            }
        }
    }
}