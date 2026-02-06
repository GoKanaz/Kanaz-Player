package com.gokanaz.kanazplayer.ui.player

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gokanaz.kanazplayer.data.model.Song
import com.gokanaz.kanazplayer.data.repository.MusicRepository
import com.gokanaz.kanazplayer.service.MusicPlayerService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MusicRepository(application)
    private val playerService = MusicPlayerService(application)
    
    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs
    
    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong
    
    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition
    
    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration
    
    private val _isShuffleEnabled = MutableStateFlow(false)
    val isShuffleEnabled: StateFlow<Boolean> = _isShuffleEnabled
    
    private val _isRepeatEnabled = MutableStateFlow(false)
    val isRepeatEnabled: StateFlow<Boolean> = _isRepeatEnabled
    
    private val _shuffledPlaylist = MutableStateFlow<List<Song>>(emptyList())
    private val _originalPlaylist = MutableStateFlow<List<Song>>(emptyList())
    
    val isPlaying = playerService.isPlaying
    
    init {
        startPositionUpdater()
    }
    
    private fun startPositionUpdater() {
        viewModelScope.launch {
            while (true) {
                if (isPlaying.value) {
                    _currentPosition.value = playerService.getCurrentPosition()
                    _duration.value = playerService.getDuration()
                }
                delay(100)
            }
        }
    }
    
    fun loadSongs() {
        viewModelScope.launch {
            val allSongs = repository.getAllSongs()
            _songs.value = allSongs
            _originalPlaylist.value = allSongs
            _shuffledPlaylist.value = allSongs.shuffled()
            if (allSongs.isNotEmpty() && _currentSong.value == null) {
                _currentSong.value = allSongs.first()
            }
        }
    }
    
    fun playSong(song: Song) {
        _currentSong.value = song
        playerService.playSong(song)
    }
    
    fun togglePlayPause() {
        _currentSong.value?.let { song ->
            if (!isPlaying.value && playerService.getCurrentPosition() == 0L) {
                playerService.playSong(song)
            } else {
                playerService.togglePlayPause()
            }
        }
    }
    
    fun seekTo(position: Long) {
        playerService.seekTo(position)
        _currentPosition.value = position
    }
    
    fun playNext() {
        if (_isRepeatEnabled.value) {
            _currentSong.value?.let { playSong(it) }
            return
        }
        
        val currentSong = _currentSong.value ?: return
        val currentList = if (_isShuffleEnabled.value) _shuffledPlaylist.value else _originalPlaylist.value
        
        val currentIndex = currentList.indexOfFirst { it.id == currentSong.id }
        if (currentIndex == -1) return
        
        val nextIndex = if (currentIndex < currentList.size - 1) {
            currentIndex + 1
        } else {
            0
        }
        
        if (nextIndex in currentList.indices) {
            playSong(currentList[nextIndex])
        }
    }
    
    fun playPrevious() {
        if (_isRepeatEnabled.value) {
            _currentSong.value?.let { playSong(it) }
            return
        }
        
        val currentSong = _currentSong.value ?: return
        val currentList = if (_isShuffleEnabled.value) _shuffledPlaylist.value else _originalPlaylist.value
        
        val currentIndex = currentList.indexOfFirst { it.id == currentSong.id }
        if (currentIndex == -1) return
        
        val prevIndex = if (currentIndex > 0) {
            currentIndex - 1
        } else {
            currentList.size - 1
        }
        
        if (prevIndex in currentList.indices) {
            playSong(currentList[prevIndex])
        }
    }
    
    fun toggleShuffle() {
        _isShuffleEnabled.value = !_isShuffleEnabled.value
        if (_isShuffleEnabled.value && _originalPlaylist.value.isNotEmpty()) {
            _shuffledPlaylist.value = _originalPlaylist.value.shuffled()
        }
    }
    
    fun toggleRepeat() {
        _isRepeatEnabled.value = !_isRepeatEnabled.value
    }
    
    override fun onCleared() {
        super.onCleared()
        playerService.release()
    }
}
