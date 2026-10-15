package com.khodehydro.faimess.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.khodehydro.faimess.model.Album
import com.khodehydro.faimess.model.Artist
import com.khodehydro.faimess.model.FandomInfo
import com.khodehydro.faimess.ui.LibraryViewModel
import com.khodehydro.faimess.ui.theme.Ink
import com.khodehydro.faimess.ui.theme.Primary
import com.khodehydro.faimess.ui.theme.Subtle

@Composable
fun ArtistsScreen(vm: LibraryViewModel, modifier: Modifier = Modifier) {
    val state by vm.state.collectAsState()

    if (state.selectedArtist != null) {
        ArtistDetailScreen(
            artist = state.selectedArtist!!,
            albums = state.artistAlbums,
            fandom = state.fandom,
            onBack = vm::clearArtistSelection,
            onToggleFollow = { vm.toggleFollow(state.selectedArtist!!.id) },
            modifier = modifier,
        )
    } else {
        ArtistListScreen(artists = state.artists, onSelect = vm::selectArtist, onToggleFollow = vm::toggleFollow, modifier = modifier)
    }
}

@Composable
private fun ArtistListScreen(artists: List<Artist>, onSelect: (String) -> Unit, onToggleFollow: (String) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Artists", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Ink)
            Spacer(Modifier.height(4.dp))
            Text("${artists.size} artists in the roster", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
        items(artists) { artist ->
            ArtistCard(artist, onClick = { onSelect(artist.id) }, onToggleFollow = { onToggleFollow(artist.id) })
        }
    }
}

@Composable
fun ArtistCard(artist: Artist, onClick: () -> Unit, onToggleFollow: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(56.dp).clip(CircleShape).background(Primary.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                Text(artist.initials, color = Primary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(artist.name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Ink)
                    if (artist.verified) { Spacer(Modifier.width(4.dp)); Text("✓", color = Primary, fontSize = 14.sp) }
                    if (artist.newRelease) { Spacer(Modifier.width(4.dp)); Box(Modifier.size(8.dp).background(Color.Red, CircleShape)) }
                }
                Text("${artist.kind} · ${artist.genre}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text("${artist.listeners} · ${artist.followers}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Button(
                onClick = onToggleFollow,
                colors = ButtonDefaults.buttonColors(containerColor = if (artist.following) Subtle else Primary, contentColor = if (artist.following) Ink else Color.White),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            ) { Text(if (artist.following) "Following" else "Follow", fontSize = 13.sp) }
        }
    }
}

@Composable
private fun ArtistDetailScreen(artist: Artist, albums: List<Album>, fandom: FandomInfo?, onBack: () -> Unit, onToggleFollow: () -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            Box(Modifier.fillMaxWidth().height(220.dp).background(Primary.copy(alpha = 0.1f))) {
                IconButton(onClick = onBack, Modifier.padding(12.dp).statusBarsPadding()) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
                Column(Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                    Text(artist.name, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Ink)
                    Text("${artist.kind} · ${artist.genre}", color = Color.Gray)
                    Text("${artist.listeners} · ${artist.followers}", color = Color.Gray, fontSize = 13.sp)
                }
                Button(
                    onClick = onToggleFollow,
                    Modifier.align(Alignment.BottomEnd).padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (artist.following) Subtle else Primary, contentColor = if (artist.following) Ink else Color.White),
                    shape = RoundedCornerShape(20.dp),
                ) { Text(if (artist.following) "Following" else "Follow") }
            }
        }
        // Fandom
        fandom?.let { f ->
            item {
                Card(Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(Modifier.padding(16.dp)) {
                        Text(" fandom", style = MaterialTheme.typography.labelMedium, color = Primary)
                        Spacer(Modifier.height(4.dp))
                        Text(f.fandomNameFa, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Ink)
                        Text(f.fandomNameEn, color = Color.Gray, fontSize = 14.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(f.mottoFa, style = MaterialTheme.typography.bodyMedium, color = Ink.copy(alpha = 0.8f))
                    }
                }
            }
        }
        // Albums
        if (albums.isNotEmpty()) {
            item {
                Text("Albums", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Ink, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            }
            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(albums) { album ->
                        AlbumCard(album, onClick = {})
                    }
                }
            }
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
fun AlbumCard(album: Album, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(160.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column {
            Box(Modifier.fillMaxWidth().height(140.dp).background(Primary.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Text(album.title.take(2), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Primary)
            }
            Column(Modifier.padding(12.dp)) {
                Text(album.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, color = Ink)
                Text(album.artist, fontSize = 12.sp, color = Color.Gray, maxLines = 1)
                Text("${album.tracks} tracks · ${album.released}", fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}