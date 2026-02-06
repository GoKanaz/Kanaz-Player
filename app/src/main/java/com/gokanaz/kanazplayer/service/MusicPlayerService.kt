package com.gokanaz.kanazplayer.service

import android.content.Context
import android.media.MediaPlayer
import com.gokanaz.kanazplayer.data.model.Song

class MusicPlayerService(private val context: Context) {
    private var onCompletionListener: (() -> Unit)? = null
    
    fun playSong(song: Song) {
        MusicPlayerManager.playSong(context, song)
    }
    
    fun togglePlayPause() {
        MusicPlayerManager.togglePlayPause(context)
    }
    
    fun seekTo(position: Long) {
        MusicPlayerManager.seekTo(position)
    }
    
    fun getCurrentPosition(): Long {
        return MusicPlayerManager.getCurrentPosition()
    }
    
    fun getDuration(): Long {
        return MusicPlayerManager.getDuration()
    }
    
    fun setPlaybackSpeed(speed: Float) {
        MusicPlayerManager.setPlaybackSpeed(speed)
    }
    
    fun getAudioSessionId(): Int {
        return MusicPlayerManager.getAudioSessionId()
    }
    
    fun setOnCompletionListener(listener: () -> Unit) {
        onCompletionListener = listener
    }
    
    fun release() {
        MusicPlayerManager.release()
    }
}
