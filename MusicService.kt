package com.gokanaz.kanazplayer.service

import android.content.Context
import com.gokanaz.kanazplayer.data.model.Song
import kotlinx.coroutines.flow.StateFlow

class MusicPlayerService(private val context: Context) {
    
    val isPlaying: StateFlow<Boolean> = MusicPlayerManager.isPlaying
    val currentPosition: StateFlow<Long> = MusicPlayerManager.currentPosition
    val duration: StateFlow<Long> = MusicPlayerManager.duration
    val currentSongState: StateFlow<Song?> = MusicPlayerManager.currentSong
    
    fun playSong(song: Song) {
        MusicPlayerManager.playSong(context, song)
    }
    
    fun togglePlayPause() {
        MusicPlayerManager.togglePlayPause(context)
    }
    
    fun seekTo(position: Long) {
        MusicPlayerManager.seekTo(context, position)
    }
    
    fun getCurrentPosition(): Long {
        return MusicPlayerManager.getCurrentPosition(context)
    }
    
    fun getDuration(): Long {
        return MusicPlayerManager.getDuration(context)
    }
    
    fun release() {
        MusicPlayerManager.release()
    }
}
