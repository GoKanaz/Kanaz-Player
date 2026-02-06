package com.gokanaz.kanazplayer.service

import android.content.Context
import androidx.media3.common.PlaybackParameters
import com.gokanaz.kanazplayer.data.model.Song
import kotlinx.coroutines.flow.StateFlow

class MusicPlayerService(private val context: Context) {
    
    private val player = MusicPlayerManager.getPlayer(context)
    
    val isPlaying: StateFlow<Boolean> = MusicPlayerManager.isPlaying
    
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
    
    fun getAudioSessionId(): Int {
        return player.audioSessionId
    }
    
    fun setPlaybackSpeed(speed: Float) {
        player.playbackParameters = PlaybackParameters(speed)
    }
    
    fun setOnCompletionListener(listener: () -> Unit) {
    }
    
    fun release() {
        MusicPlayerManager.release()
    }
}
