package com.gokanaz.kanazplayer.data.repository

import android.content.Context
import com.gokanaz.kanazplayer.data.model.Playlist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PlaylistRepository(private val context: Context) {
    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists
    
    private var nextId = 1L
    
    fun createPlaylist(name: String) {
        val newPlaylist = Playlist(
            id = nextId++,
            name = name,
            songIds = emptyList()
        )
        _playlists.value = _playlists.value + newPlaylist
    }
    
    fun addSongToPlaylist(playlistId: Long, songId: Long) {
        _playlists.value = _playlists.value.map { playlist ->
            if (playlist.id == playlistId) {
                playlist.copy(songIds = playlist.songIds + songId)
            } else {
                playlist
            }
        }
    }
    
    fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        _playlists.value = _playlists.value.map { playlist ->
            if (playlist.id == playlistId) {
                playlist.copy(songIds = playlist.songIds - songId)
            } else {
                playlist
            }
        }
    }
    
    fun deletePlaylist(playlistId: Long) {
        _playlists.value = _playlists.value.filter { it.id != playlistId }
    }
}
