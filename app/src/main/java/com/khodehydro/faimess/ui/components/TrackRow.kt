package com.khodehydro.faimess.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.khodehydro.faimess.R
import com.khodehydro.faimess.model.Playlist
import com.khodehydro.faimess.model.Track
import com.khodehydro.faimess.model.formatClock
import com.khodehydro.faimess.model.formatLength
import com.khodehydro.faimess.ui.theme.Ink
import com.khodehydro.faimess.ui.theme.InkFaint
import com.khodehydro.faimess.ui.theme.InkMuted
import com.khodehydro.faimess.ui.theme.Line

/** "6 tracks · 21 min · Mellow" — the facts line the site prints under a playlist's name. */
@Composable
fun playlistFacts(playlist: Playlist): String = stringResource(
    R.string.playlist_facts,
    pluralStringResource(R.plurals.track_count, playlist.tracks.size, playlist.tracks.size),
    formatLength(playlist.totalSeconds),
    playlist.mood,
)

/**
 * One song, drawn like the site's detail rows: index, square artwork, title over artist · album,
 * and the clock at the end. White on a soft panel; [compact] is the feed-card size.
 */
@Composable
fun TrackRow(
    index: Int,
    track: Track,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val shape = RoundedCornerShape(14.dp)
    val artSize = if (compact) 36.dp else 44.dp
    val description = stringResource(R.string.play_track, track.title, track.artist)
    Row(
        modifier = modifier.fillMaxWidth().clip(shape).background(Color.White).border(1.dp, Line, shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(role = Role.Button, onClick = onClick).semantics { contentDescription = description }
                } else {
                    Modifier
                },
            )
            .padding(horizontal = 10.dp, vertical = if (compact) 6.dp else 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            index.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = InkFaint,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(16.dp),
        )
        OfflinePhoto(track.cover, null, Modifier.size(artSize).clip(RoundedCornerShape(if (compact) 10.dp else 12.dp)))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(
                track.title,
                style = MaterialTheme.typography.titleSmall,
                color = Ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                if (compact) track.artist else "${track.artist} · ${track.album}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = InkMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(formatClock(track.seconds), style = MaterialTheme.typography.labelMedium, color = InkFaint)
    }
}

/** A small square cover with the site's 12 dp thumb radius. */
@Composable
fun PlaylistCover(playlist: Playlist, size: Dp, modifier: Modifier = Modifier, radius: Dp = 14.dp) {
    Box(modifier.size(size).clip(RoundedCornerShape(radius))) {
        OfflinePhoto(playlist.cover, null, Modifier.size(size))
    }
}
