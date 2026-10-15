package com.khodehydro.faimess.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.khodehydro.faimess.R
import com.khodehydro.faimess.ui.PlayerUiState
import com.khodehydro.faimess.ui.theme.Primary
import com.khodehydro.faimess.ui.theme.PrimaryDeep

/**
 * Compact player bar in a fully-rounded purple capsule — matching the FAIMESS
 * web mini-player (src/sections/MiniPlayer.tsx).
 *
 * The Row mirrors FloatingNavigation's own layout: a menu-sized capsule
 * (max 236dp, same as the nav menu) followed by a 10dp spacer and a
 * 62dp placeholder so the mini player is exactly the same width as
 * the nav.  Purple background, white text, circular cover.
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
        // Same Row skeleton as FloatingNavigation: menu-capsule (≤236dp) + 10dp gap + 62dp circle
        Row(verticalAlignment = Alignment.CenterVertically) {
            // The purple capsule — same max width as the nav menu
            Box(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .widthIn(max = 236.dp)
                    .fillMaxWidth()
                    .shadow(
                        18.dp,
                        RoundedCornerShape(50),
                        ambientColor = PrimaryDeep.copy(alpha = 0.45f),
                        spotColor = PrimaryDeep.copy(alpha = 0.68f),
                    )
                    .clip(RoundedCornerShape(50))
                    .background(Primary)
                    .testTag("mini_player"),
            ) {
                // Full-bar clickable → open the full player
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
                            .padding(start = 6.dp, end = 10.dp, top = 6.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Circular cover art
                        OfflinePhoto(
                            track.cover,
                            null,
                            Modifier
                                .size(42.dp)
                                .clip(CircleShape),
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
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                track.artist,
                                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White.copy(alpha = 0.75f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }

                        // Play / Pause — white circle with purple icon
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.White)
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
                                tint = PrimaryDeep,
                            )
                        }

                        Spacer(Modifier.width(2.dp))

                        // Next
                        Box(
                            modifier = Modifier
                                .size(32.dp)
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
                                modifier = Modifier.size(16.dp),
                                tint = Color.White.copy(alpha = 0.8f),
                            )
                        }
                    }

                    // Thin white progress bar
                    LinearProgressIndicator(
                        progress = { state.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.25f),
                        strokeCap = StrokeCap.Round,
                    )
                }
            }

            // 10dp gap — same as between menu and search in the nav
            Spacer(Modifier.width(10.dp))

            // 62dp placeholder — same size as the search circle in the nav
            Spacer(Modifier.size(62.dp))
        }
    }
}