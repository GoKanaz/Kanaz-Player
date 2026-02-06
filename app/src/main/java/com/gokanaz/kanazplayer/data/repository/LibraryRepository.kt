package com.gokanaz.kanazplayer.data.repository

import android.content.Context
import com.gokanaz.kanazplayer.data.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class MusicFolder(
    val name: String,
    val path: String,
    val songCount: Int,
    val songs: List<Song>
)

data class Album(
    val name: String,
    val artist: String,
    val songCount: Int,
    val songs: List<Song>
)

data class Artist(
    val name: String,
    val albumCount: Int,
    val songCount: Int,
    val songs: List<Song>
)

data class Genre(
    val name: String,
    val songCount: Int,
    val songs: List<Song>
)

class LibraryRepository(private val context: Context) {
    
    private val _folders = MutableStateFlow<List<MusicFolder>>(emptyList())
    val folders: StateFlow<List<MusicFolder>> = _folders
    
    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums
    
    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists: StateFlow<List<Artist>> = _artists
    
    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> = _genres
    
    fun updateLibrary(songs: List<Song>) {
        updateFolders(songs)
        updateAlbums(songs)
        updateArtists(songs)
        updateGenres(songs)
    }
    
    private fun updateFolders(songs: List<Song>) {
        val foldersMap = songs.groupBy { song ->
            val file = java.io.File(song.path)
            file.parent ?: "Unknown"
        }
        
        _folders.value = foldersMap.map { (path, songList) ->
            val folderName = path.split("/").lastOrNull() ?: "Unknown"
            MusicFolder(
                name = folderName,
                path = path,
                songCount = songList.size,
                songs = songList
            )
        }.sortedBy { it.name }
    }
    
    private fun updateAlbums(songs: List<Song>) {
        val albumsMap = songs.groupBy { it.album }
        
        _albums.value = albumsMap.map { (albumName, songList) ->
            Album(
                name = albumName,
                artist = songList.firstOrNull()?.artist ?: "Unknown",
                songCount = songList.size,
                songs = songList.sortedBy { it.title }
            )
        }.sortedBy { it.name }
    }
    
    private fun updateArtists(songs: List<Song>) {
        val artistsMap = songs.groupBy { it.artist }
        
        _artists.value = artistsMap.map { (artistName, songList) ->
            Artist(
                name = artistName,
                albumCount = songList.map { it.album }.distinct().size,
                songCount = songList.size,
                songs = songList.sortedBy { it.title }
            )
        }.sortedBy { it.name }
    }
    
    private fun updateGenres(songs: List<Song>) {
        val genresMap = songs.groupBy { song ->
            when {
                song.title.contains("metal", ignoreCase = true) || 
                song.artist.contains("metal", ignoreCase = true) -> "Heavy Metal"
                song.title.contains("rock", ignoreCase = true) || 
                song.artist.contains("rock", ignoreCase = true) -> "Rock"
                song.title.contains("pop", ignoreCase = true) || 
                song.artist.contains("pop", ignoreCase = true) -> "Pop"
                song.title.contains("jazz", ignoreCase = true) || 
                song.artist.contains("jazz", ignoreCase = true) -> "Jazz"
                song.title.contains("classical", ignoreCase = true) || 
                song.artist.contains("classical", ignoreCase = true) -> "Classical"
                else -> "Other"
            }
        }
        
        _genres.value = genresMap.map { (genreName, songList) ->
            Genre(
                name = genreName,
                songCount = songList.size,
                songs = songList.sortedBy { it.title }
            )
        }.sortedBy { it.name }
    }
}
