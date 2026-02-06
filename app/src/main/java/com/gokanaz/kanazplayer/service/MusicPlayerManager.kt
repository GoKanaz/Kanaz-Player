package com.gokanaz.kanazplayer.service

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import androidx.media3.common.AudioAttributes as Media3AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.gokanaz.kanazplayer.data.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object MusicPlayerManager {
    private var exoPlayer: ExoPlayer? = null
    private var audioManager: AudioManager? = null
    private var audioFocusRequest: AudioFocusRequest? = null
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying
    
    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition
    
    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration
    
    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong
    
    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                exoPlayer?.volume = 1.0f
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                exoPlayer?.pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                exoPlayer?.volume = 0.3f
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                exoPlayer?.pause()
            }
        }
    }
    
    fun getPlayer(context: Context): ExoPlayer {
        if (exoPlayer == null) {
            audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            
            val audioAttributes = Media3AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .build()
            
            exoPlayer = ExoPlayer.Builder(context.applicationContext)
                .setAudioAttributes(audioAttributes, true)
                .setHandleAudioBecomingNoisy(true)
                .setWakeMode(C.WAKE_MODE_LOCAL)
                .build().apply {
                    addListener(object : Player.Listener {
                        override fun onIsPlayingChanged(playing: Boolean) {
                            _isPlaying.value = playing
                        }
                    })
                }
        }
        return exoPlayer!!
    }
    
    private fun requestAudioFocus(context: Context): Boolean {
        audioManager = audioManager ?: context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
            
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes)
                .setOnAudioFocusChangeListener(audioFocusChangeListener)
                .build()
            
            audioManager?.requestAudioFocus(audioFocusRequest!!) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } else {
            @Suppress("DEPRECATION")
            audioManager?.requestAudioFocus(
                audioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
    }
    
    fun playSong(context: Context, song: Song) {
        val player = getPlayer(context)
        
        if (requestAudioFocus(context)) {
            try {
                val mediaMetadata = MediaMetadata.Builder()
                    .setTitle(song.title)
                    .setArtist(song.artist)
                    .setAlbumTitle(song.album)
                    .build()
                
                val mediaItem = MediaItem.Builder()
                    .setUri(song.path)
                    .setMediaMetadata(mediaMetadata)
                    .build()
                
                player.setMediaItem(mediaItem)
                player.prepare()
                player.playWhenReady = true
                _currentSong.value = song
                _duration.value = song.duration
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun togglePlayPause(context: Context) {
        exoPlayer?.let { player ->
            if (player.playWhenReady) {
                player.playWhenReady = false
            } else {
                if (requestAudioFocus(context)) {
                    player.playWhenReady = true
                }
            }
        }
    }
    
    fun seekTo(context: Context, position: Long) {
        exoPlayer?.seekTo(position)
        _currentPosition.value = position
    }
    
    fun getCurrentPosition(context: Context): Long {
        return exoPlayer?.currentPosition ?: 0L
    }
    
    fun getDuration(context: Context): Long {
        val duration = exoPlayer?.duration ?: 0L
        return if (duration > 0) duration else 0
    }
    
    fun release() {
        exoPlayer?.release()
        exoPlayer = null
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { audioManager?.abandonAudioFocusRequest(it) }
        } else {
            @Suppress("DEPRECATION")
            audioManager?.abandonAudioFocus(audioFocusChangeListener)
        }
        
        _isPlaying.value = false
    }
}
