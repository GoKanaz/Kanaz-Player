package com.gokanaz.kanazplayer.feature.player

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_BUFFERING
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.common.Player.STATE_IDLE
import androidx.media3.common.Player.STATE_READY
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import com.gokanaz.kanazplayer.core.common.result.Result
import com.gokanaz.kanazplayer.core.data.model.Song
import com.gokanaz.kanazplayer.core.domain.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : ViewModel() {

    private val _playerState = MutableStateFlow<Result<PlayerState>>(Result.Loading)
    val playerState: StateFlow<Result<PlayerState>> = _playerState.asStateFlow()

    private var mediaControllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null
    private var progressUpdateJob: Job? = null

    data class PlayerState(
        val currentSong: Song? = null,
        val playbackState: Int = STATE_IDLE,
        val isPlaying: Boolean = false,
        val currentPosition: Long = 0L,
        val duration: Long = 0L,
        val bufferedPercentage: Int = 0,
        val repeatMode: Int = Player.REPEAT_MODE_OFF,
        val shuffleModeEnabled: Boolean = false,
        val playbackError: Throwable? = null
    )

    fun initializeMediaController(context: Context) {
        viewModelScope.launch {
            try {
                mediaControllerFuture = MediaController.Builder(
                    context,
                    MediaController.getSessionActivity(context, null)
                ).buildAsync()
                mediaController = mediaControllerFuture?.get()
                mediaController?.addListener(PlayerListener())
                updatePlayerState()
                startProgressUpdates()
            } catch (e: Exception) {
                _playerState.value = Result.Error(e)
            }
        }
    }

    fun playSong(song: Song) {
        viewModelScope.launch {
            try {
                mediaController?.setMediaItem(song.toMediaItem())
                mediaController?.prepare()
                mediaController?.play()
                updatePlayerState()
            } catch (e: Exception) {
                _playerState.value = Result.Error(e)
            }
        }
    }

    fun playSongList(songs: List<Song>, startIndex: Int = 0) {
        viewModelScope.launch {
            try {
                val mediaItems = songs.map { it.toMediaItem() }
                mediaController?.setMediaItems(mediaItems, startIndex, 0L)
                mediaController?.prepare()
                mediaController?.play()
                updatePlayerState()
            } catch (e: Exception) {
                _playerState.value = Result.Error(e)
            }
        }
    }

    fun togglePlayPause() {
        viewModelScope.launch {
            try {
                if (mediaController?.isPlaying == true) {
                    mediaController?.pause()
                } else {
                    mediaController?.play()
                }
                updatePlayerState()
            } catch (e: Exception) {
                _playerState.value = Result.Error(e)
            }
        }
    }

    fun seekTo(position: Long) {
        viewModelScope.launch {
            try {
                mediaController?.seekTo(position)
                updatePlayerState()
            } catch (e: Exception) {
                _playerState.value = Result.Error(e)
            }
        }
    }

    fun skipToNext() {
        viewModelScope.launch {
            try {
                mediaController?.seekToNextMediaItem()
                updatePlayerState()
            } catch (e: Exception) {
                _playerState.value = Result.Error(e)
            }
        }
    }

    fun skipToPrevious() {
        viewModelScope.launch {
            try {
                mediaController?.seekToPreviousMediaItem()
                updatePlayerState()
            } catch (e: Exception) {
                _playerState.value = Result.Error(e)
            }
        }
    }

    fun setRepeatMode(repeatMode: Int) {
        viewModelScope.launch {
            try {
                mediaController?.repeatMode = repeatMode
                updatePlayerState()
            } catch (e: Exception) {
                _playerState.value = Result.Error(e)
            }
        }
    }

    fun toggleShuffle() {
        viewModelScope.launch {
            try {
                mediaController?.shuffleModeEnabled = !(mediaController?.shuffleModeEnabled ?: false)
                updatePlayerState()
            } catch (e: Exception) {
                _playerState.value = Result.Error(e)
            }
        }
    }

    private fun updatePlayerState() {
        val controller = mediaController ?: return
        val currentMediaItem = controller.currentMediaItem
        val currentSong = if (currentMediaItem != null) {
            Song.fromMediaItem(currentMediaItem)
        } else {
            null
        }

        _playerState.update { result ->
            when (result) {
                is Result.Success -> {
                    Result.Success(
                        PlayerState(
                            currentSong = currentSong,
                            playbackState = controller.playbackState,
                            isPlaying = controller.isPlaying,
                            currentPosition = controller.currentPosition,
                            duration = controller.duration,
                            bufferedPercentage = controller.bufferedPercentage,
                            repeatMode = controller.repeatMode,
                            shuffleModeEnabled = controller.shuffleModeEnabled,
                            playbackError = null
                        )
                    )
                }
                else -> Result.Success(
                    PlayerState(
                        currentSong = currentSong,
                        playbackState = controller.playbackState,
                        isPlaying = controller.isPlaying,
                        currentPosition = controller.currentPosition,
                        duration = controller.duration,
                        bufferedPercentage = controller.bufferedPercentage,
                        repeatMode = controller.repeatMode,
                        shuffleModeEnabled = controller.shuffleModeEnabled
                    )
                )
            }
        }
    }

    private fun startProgressUpdates() {
        progressUpdateJob?.cancel()
        progressUpdateJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (mediaController?.isPlaying == true) {
                    updatePlayerState()
                }
            }
        }
    }

    private inner class PlayerListener : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            updatePlayerState()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            updatePlayerState()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            updatePlayerState()
        }

        override fun onPlaybackSuppressionReasonChanged(playbackSuppressionReason: Int) {
            super.onPlaybackSuppressionReasonChanged(playbackSuppressionReason)
            updatePlayerState()
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            super.onRepeatModeChanged(repeatMode)
            updatePlayerState()
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            super.onShuffleModeEnabledChanged(shuffleModeEnabled)
            updatePlayerState()
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            _playerState.update { result ->
                when (result) {
                    is Result.Success -> Result.Success(result.data.copy(playbackError = error))
                    else -> Result.Error(error)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        progressUpdateJob?.cancel()
        mediaController?.removeListener(PlayerListener())
        mediaController?.release()
        mediaControllerFuture?.cancel(true)
    }
}