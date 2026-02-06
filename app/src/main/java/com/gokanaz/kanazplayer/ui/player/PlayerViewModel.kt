package com.gokanaz.kanazplayer.ui.player

import android.app.Application
import android.graphics.Bitmap
import android.media.audiofx.BassBoost
import android.media.audiofx.Virtualizer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gokanaz.kanazplayer.data.model.Playlist
import com.gokanaz.kanazplayer.data.model.Song
import com.gokanaz.kanazplayer.data.repository.*
import com.gokanaz.kanazplayer.service.MusicPlayerManager
import com.gokanaz.kanazplayer.service.MusicPlayerService
import com.gokanaz.kanazplayer.service.SleepTimerManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MusicRepository(application)
    private val playlistRepository = PlaylistRepository(application)
    private val libraryRepository = LibraryRepository(application)
    private val playerService = MusicPlayerService(application)
    private val context = application
    
    private var bassBoostEffect: BassBoost? = null
    private var virtualizerEffect: Virtualizer? = null
    
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
    
    private val _albumArt = MutableStateFlow<Bitmap?>(null)
    val albumArt: StateFlow<Bitmap?> = _albumArt
    
    private val _queue = MutableStateFlow<List<Song>>(emptyList())
    val queue: StateFlow<List<Song>> = _queue
    
    private val _equalizerEnabled = MutableStateFlow(false)
    val equalizerEnabled: StateFlow<Boolean> = _equalizerEnabled
    
    private val _bassBoost = MutableStateFlow(0)
    val bassBoost: StateFlow<Int> = _bassBoost
    
    private val _virtualizerStrength = MutableStateFlow(0)
    val virtualizerStrength: StateFlow<Int> = _virtualizerStrength
    
    val playlists: StateFlow<List<Playlist>> = playlistRepository.playlists
    val folders: StateFlow<List<MusicFolder>> = libraryRepository.folders
    val albums: StateFlow<List<Album>> = libraryRepository.albums
    val artists: StateFlow<List<Artist>> = libraryRepository.artists
    val genres: StateFlow<List<Genre>> = libraryRepository.genres
    
    val isPlaying: StateFlow<Boolean> = playerService.isPlaying
    val sleepTimerActive: StateFlow<Boolean> = SleepTimerManager.isActive
    val sleepTimerRemaining: StateFlow<Long> = SleepTimerManager.remainingTime
    
    init {
        startPositionUpdater()
        observeCurrentSong()
        initializeAudioEffects()
        setupCompletionListener()
    }
    
    private fun setupCompletionListener() {
        playerService.setOnCompletionListener {
            playNext()
        }
    }
    
    private fun initializeAudioEffects() {
        try {
            val audioSessionId = playerService.getAudioSessionId()
            bassBoostEffect = BassBoost(0, audioSessionId).apply {
                enabled = false
            }
            virtualizerEffect = Virtualizer(0, audioSessionId).apply {
                enabled = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun setEqualizerEnabled(enabled: Boolean) {
        _equalizerEnabled.value = enabled
        bassBoostEffect?.enabled = enabled
        virtualizerEffect?.enabled = enabled
    }
    
    fun setBassBoost(strength: Int) {
        _bassBoost.value = strength
        bassBoostEffect?.setStrength(strength.toShort())
    }
    
    fun setVirtualizerStrength(strength: Int) {
        _virtualizerStrength.value = strength
        virtualizerEffect?.setStrength(strength.toShort())
    }
    
    private fun observeCurrentSong() {
        viewModelScope.launch {
            MusicPlayerManager.currentSong.collect { song ->
                _currentSong.value = song
                song?.let { loadAlbumArt(it) }
            }
        }
    }
    
    private fun loadAlbumArt(song: Song) {
        viewModelScope.launch {
            _albumArt.value = repository.getAlbumArt(song)
        }
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
            _songs.value = repository.getAllSongs()
            libraryRepository.updateLibrary(_songs.value)
            if (_songs.value.isNotEmpty() && _currentSong.value == null) {
                _currentSong.value = _songs.value.first()
            }
            updateQueue()
        }
    }
    
    private fun updateQueue() {
        val currentIndex = _songs.value.indexOf(_currentSong.value)
        if (currentIndex >= 0) {
            _queue.value = _songs.value.drop(currentIndex)
        }
    }
    
    fun playSong(song: Song) {
        _currentSong.value = song
        playerService.playSong(song)
        loadAlbumArt(song)
        updateQueue()
    }
    
    fun playSongs(songs: List<Song>, startIndex: Int = 0) {
        if (songs.isNotEmpty() && startIndex < songs.size) {
            _songs.value = songs
            playSong(songs[startIndex])
        }
    }
    
    fun togglePlayPause() {
        if (_currentSong.value == null && _songs.value.isNotEmpty()) {
            playSong(_songs.value.first())
        } else {
            playerService.togglePlayPause()
        }
    }
    
    fun seekTo(position: Long) {
        playerService.seekTo(position)
        _currentPosition.value = position
    }
    
    fun playNext() {
        if (_songs.value.isEmpty()) return
        
        if (_isRepeatEnabled.value) {
            _currentSong.value?.let { playSong(it) }
            return
        }
        
        val currentIndex = _songs.value.indexOf(_currentSong.value)
        val nextIndex = if (_isShuffleEnabled.value) {
            (0 until _songs.value.size).filter { it != currentIndex }.randomOrNull() ?: 0
        } else {
            if (currentIndex < _songs.value.size - 1) currentIndex + 1 else 0
        }
        
        if (nextIndex < _songs.value.size) {
            playSong(_songs.value[nextIndex])
        }
    }
    
    fun playPrevious() {
        if (_songs.value.isEmpty()) return
        
        val currentIndex = _songs.value.indexOf(_currentSong.value)
        val prevIndex = if (currentIndex > 0) currentIndex - 1 else _songs.value.size - 1
        
        if (prevIndex >= 0 && prevIndex < _songs.value.size) {
            playSong(_songs.value[prevIndex])
        }
    }
    
    fun removeFromQueue(index: Int) {
        val mutableQueue = _queue.value.toMutableList()
        if (index in mutableQueue.indices) {
            mutableQueue.removeAt(index)
            _queue.value = mutableQueue
        }
    }
    
    fun clearQueue() {
        _queue.value = listOf(_currentSong.value).filterNotNull()
    }
    
    fun toggleShuffle() {
        _isShuffleEnabled.value = !_isShuffleEnabled.value
    }
    
    fun toggleRepeat() {
        _isRepeatEnabled.value = !_isRepeatEnabled.value
    }
    
    fun setSleepTimer(minutes: Int) {
        SleepTimerManager.startTimer(context, minutes)
    }
    
    fun cancelSleepTimer() {
        SleepTimerManager.cancelTimer()
    }
    
    fun createPlaylist(name: String) {
        playlistRepository.createPlaylist(name)
    }
    
    fun addSongToPlaylist(playlistId: Long, songId: Long) {
        playlistRepository.addSongToPlaylist(playlistId, songId)
    }
    
    override fun onCleared() {
        super.onCleared()
        bassBoostEffect?.release()
        virtualizerEffect?.release()
        playerService.release()
    }
}
