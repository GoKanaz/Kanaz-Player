package com.gokanaz.kanazplayer.service

import android.content.Context
import android.media.MediaPlayer
import com.gokanaz.kanazplayer.data.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MusicPlayerService(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var onCompletionListener: (() -> Unit)? = null
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying
    
    fun setOnCompletionListener(listener: () -> Unit) {
        onCompletionListener = listener
    }
    
    fun playSong(song: Song) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(song.path)
                prepare()
                start()
                setOnCompletionListener {
                    _isPlaying.value = false
                    onCompletionListener?.invoke()
                }
            }
            _isPlaying.value = true
        } catch (e: Exception) {
            e.printStackTrace()
            _isPlaying.value = false
        }
    }
    
    fun togglePlayPause() {
        mediaPlayer?.let {
            try {
                if (it.isPlaying) {
                    it.pause()
                    _isPlaying.value = false
                } else {
                    it.start()
                    _isPlaying.value = true
                }
            } catch (e: IllegalStateException) {
                e.printStackTrace()
                _isPlaying.value = false
            }
        }
    }
    
    fun seekTo(position: Long) {
        mediaPlayer?.let {
            try {
                it.seekTo(position.toInt())
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
    }
    
    fun getCurrentPosition(): Long {
        return try {
            mediaPlayer?.currentPosition?.toLong() ?: 0L
        } catch (e: IllegalStateException) {
            0L
        }
    }
    
    fun getDuration(): Long {
        return try {
            mediaPlayer?.duration?.toLong() ?: 0L
        } catch (e: IllegalStateException) {
            0L
        }
    }
    
    fun getAudioSessionId(): Int {
        return try {
            mediaPlayer?.audioSessionId ?: 0
        } catch (e: IllegalStateException) {
            0
        }
    }
    
    fun release() {
        try {
            mediaPlayer?.release()
            mediaPlayer = null
            _isPlaying.value = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
