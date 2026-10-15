package com.khodehydro.faimess.ui

import androidx.lifecycle.ViewModel
import com.khodehydro.faimess.data.*
import com.khodehydro.faimess.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/* ------------------------------------------------------------------ *
 *  Library ViewModel — Artists, Albums, Playlists, Fandoms
 * ------------------------------------------------------------------ */

data class LibraryUiState(
    val artists: List<Artist> = ALL_ARTISTS,
    val albums: List<Album> = ALL_ALBUMS,
    val playlists: List<LibraryPlaylist> = CURATED_PLAYLISTS,
    val userPlaylists: List<UserPlaylist> = emptyList(),
    val selectedArtist: Artist? = null,
    val selectedAlbum: Album? = null,
    val artistAlbums: List<Album> = emptyList(),
    val fandom: FandomInfo? = null,
    val followedArtists: List<Artist> = ALL_ARTISTS.filter { it.following },
    val searchQuery: String = "",
    val searchResults: List<Any> = emptyList(),
)

class LibraryViewModel : ViewModel() {
    private val _state = MutableStateFlow(LibraryUiState())
    val state: StateFlow<LibraryUiState> = _state.asStateFlow()

    fun selectArtist(id: String) {
        val artist = ALL_ARTISTS.find { it.id == id } ?: return
        val albums = ALL_ALBUMS.filter { it.artist.equals(artist.name, ignoreCase = true) }
        val fandom = ARTIST_FANDOMS[id]
        _state.update { it.copy(selectedArtist = artist, artistAlbums = albums, fandom = fandom) }
    }

    fun clearArtistSelection() = _state.update { it.copy(selectedArtist = null, artistAlbums = emptyList(), fandom = null) }

    fun selectAlbum(id: String) {
        val album = ALL_ALBUMS.find { it.id == id } ?: return
        _state.update { it.copy(selectedAlbum = album) }
    }

    fun clearAlbumSelection() = _state.update { it.copy(selectedAlbum = null) }

    fun toggleFollow(artistId: String) {
        _state.update { state ->
            val updated = state.artists.map { if (it.id == artistId) it.copy(following = !it.following) else it }
            state.copy(artists = updated, followedArtists = updated.filter { it.following })
        }
    }

    fun search(query: String) {
        val q = query.trim().lowercase()
        _state.update { state ->
            if (q.isEmpty()) return@update state.copy(searchQuery = query, searchResults = emptyList())
            val matchedArtists = ALL_ARTISTS.filter { it.name.lowercase().contains(q) || it.genre.lowercase().contains(q) }
            val matchedAlbums = ALL_ALBUMS.filter { it.title.lowercase().contains(q) || it.artist.lowercase().contains(q) }
            val matchedPlaylists = CURATED_PLAYLISTS.filter { it.name.lowercase().contains(q) }
            state.copy(searchQuery = query, searchResults = matchedArtists + matchedAlbums + matchedPlaylists)
        }
    }

    fun createPlaylist(name: String, trackIds: List<String>) {
        val pl = UserPlaylist("pl-usr-${System.currentTimeMillis()}", name, "me", trackIds = trackIds)
        _state.update { it.copy(userPlaylists = it.userPlaylists + pl) }
    }
}