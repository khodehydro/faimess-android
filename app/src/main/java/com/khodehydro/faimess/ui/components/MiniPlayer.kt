package com.khodehydro.faimess.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.khodehydro.faimess.R
import com.khodehydro.faimess.data.DemoContent
import com.khodehydro.faimess.model.Track
import com.khodehydro.faimess.ui.theme.FaimessTheme
import com.khodehydro.faimess.ui.theme.Primary
import com.khodehydro.faimess.ui.theme.PrimaryDeep

/** The mini bar is as tall as the menu capsule, so the bottom stack reads as two matching pills. */
val MiniPlayerHeight = 62.dp

/**
 * The site's mini player: a brand-purple capsule that rises above the menu once a song is loaded.
 * Round artwork, the song over its artist, a white play/pause circle and a next button; tapping
 * anywhere else opens the full player. There is no progress strip here, like on the site.
 */
@Composable
fun MiniPlayer(
    track: Track,
    playing: Boolean,
    onToggle: () -> Unit,
    onNext: () -> Unit,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(22.dp)
    val openLabel = stringResource(R.string.open_player)
    Row(
        modifier = modifier.fillMaxWidth().height(MiniPlayerHeight)
            .shadow(18.dp, shape, ambientColor = PrimaryDeep.copy(alpha = 0.45f), spotColor = PrimaryDeep.copy(alpha = 0.6f))
            .clip(shape).background(Primary).border(1.dp, Color.White.copy(alpha = 0.2f), shape)
            .clickable(role = Role.Button, onClick = onOpen)
            .semantics { contentDescription = openLabel }
            .testTag("mini_player")
            .padding(start = 10.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        OfflinePhoto(track.cover, null, Modifier.size(42.dp).clip(CircleShape))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(
                track.title,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                stringResource(R.string.track_subtitle, track.artist, track.album),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.75f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        val toggleLabel = stringResource(if (playing) R.string.pause else R.string.play)
        Box(
            modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.White)
                .clickable(role = Role.Button, onClick = onToggle)
                .semantics { contentDescription = toggleLabel }
                .testTag("mini_toggle"),
            contentAlignment = Alignment.Center,
        ) {
            AppIcon(
                if (playing) R.drawable.ic_pause else R.drawable.ic_play,
                modifier = Modifier.size(if (playing) 20.dp else 16.dp).padding(start = if (playing) 0.dp else 2.dp),
                tint = PrimaryDeep,
            )
        }
        val nextLabel = stringResource(R.string.next_track)
        Box(
            modifier = Modifier.size(36.dp).clip(CircleShape)
                .clickable(role = Role.Button, onClick = onNext)
                .semantics { contentDescription = nextLabel }
                .testTag("mini_next"),
            contentAlignment = Alignment.Center,
        ) {
            AppIcon(R.drawable.ic_skip_next, modifier = Modifier.size(24.dp), tint = Color.White.copy(alpha = 0.85f))
        }
    }
}

@Preview(widthDp = 360)
@Composable
private fun MiniPlayerPreview() {
    FaimessTheme {
        MiniPlayer(DemoContent.posts.first().playlist.tracks.first(), playing = true, {}, {}, {}, Modifier.padding(16.dp))
    }
}
