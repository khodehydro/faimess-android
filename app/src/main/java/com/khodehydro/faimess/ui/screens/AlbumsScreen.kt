package com.khodehydro.faimess.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.khodehydro.faimess.model.Album
import com.khodehydro.faimess.ui.LibraryViewModel
import com.khodehydro.faimess.ui.theme.Ink
import com.khodehydro.faimess.ui.theme.Primary

@Composable
fun AlbumsScreen(vm: LibraryViewModel, modifier: Modifier = Modifier) {
    val state by vm.state.collectAsState()
    val albums = state.albums
    val grouped = albums.groupBy { it.year }

    LazyColumn(modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("Albums", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Ink)
            Spacer(Modifier.height(4.dp))
            Text("${albums.size} albums", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
        grouped.forEach { (year, yearAlbums) ->
            item {
                Text("$year", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Ink, modifier = Modifier.padding(top = 8.dp))
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(yearAlbums) { album ->
                        AlbumCard(album, onClick = { vm.selectAlbum(album.id) })
                    }
                }
            }
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}