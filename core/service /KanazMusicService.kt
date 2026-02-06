package com.gokanaz.kanazplayer.core.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.gokanaz.kanazplayer.MainActivity
import com.gokanaz.kanazplayer.R
import com.gokanaz.kanazplayer.core.data.model.Song
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class KanazMusicService : MediaSessionService() {

    @Inject
    lateinit var exoPlayer: ExoPlayer

    private var mediaSession: MediaSession? = null
    private var currentSong: Song? = null

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "kanaz_music_channel"
        const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        initializeExoPlayer()
        initializeMediaSession()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Kanaz Music",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Background music playback"
                setShowBadge(false)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun initializeExoPlayer() {
        exoPlayer.apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                true
            )
            repeatMode = Player.REPEAT_MODE_OFF
            addListener(object : Player.Listener {
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                    updateNotification()
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    updateNotification()
                }

                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    error.printStackTrace()
                }
            })
        }
    }

    private fun initializeMediaSession() {
        mediaSession = MediaSession.Builder(this, exoPlayer)
            .setCallback(MediaSessionCallback())
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession?.run {
            release()
            mediaSession = null
        }
        exoPlayer.release()
    }

    fun playSong(song: Song) {
        currentSong = song
        val mediaSourceFactory = ProgressiveMediaSource.Factory(
            DefaultDataSource.Factory(this)
        )
        val mediaItem = song.toMediaItem()
        exoPlayer.setMediaSource(mediaSourceFactory.createMediaSource(mediaItem))
        exoPlayer.prepare()
        exoPlayer.play()
        updateNotification()
    }

    fun pause() {
        exoPlayer.pause()
        updateNotification()
    }

    fun resume() {
        exoPlayer.play()
        updateNotification()
    }

    fun stop() {
        exoPlayer.stop()
        stopSelf()
    }

    fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
    }

    fun skipToNext() {
        if (exoPlayer.hasNextMediaItem()) {
            exoPlayer.seekToNextMediaItem()
        }
    }

    fun skipToPrevious() {
        if (exoPlayer.hasPreviousMediaItem()) {
            exoPlayer.seekToPreviousMediaItem()
        }
    }

    fun setRepeatMode(repeatMode: Int) {
        exoPlayer.repeatMode = repeatMode
    }

    fun setShuffleModeEnabled(shuffleModeEnabled: Boolean) {
        exoPlayer.shuffleModeEnabled = shuffleModeEnabled
    }

    private fun updateNotification() {
        val notification = buildNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseAction = if (exoPlayer.isPlaying) {
            NotificationCompat.Action(
                R.drawable.ic_pause,
                "Pause",
                MediaSessionActionReceiver.getPlaybackActionPendingIntent(
                    this,
                    MediaSessionActionReceiver.ACTION_PAUSE
                )
            )
        } else {
            NotificationCompat.Action(
                R.drawable.ic_play,
                "Play",
                MediaSessionActionReceiver.getPlaybackActionPendingIntent(
                    this,
                    MediaSessionActionReceiver.ACTION_PLAY
                )
            )
        }

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(currentSong?.title ?: "Kanaz Player")
            .setContentText(currentSong?.artist ?: "No song playing")
            .setSmallIcon(R.drawable.ic_music_note)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOngoing(exoPlayer.isPlaying)
            .addAction(
                NotificationCompat.Action(
                    R.drawable.ic_skip_previous,
                    "Previous",
                    MediaSessionActionReceiver.getPlaybackActionPendingIntent(
                        this,
                        MediaSessionActionReceiver.ACTION_PREVIOUS
                    )
                )
            )
            .addAction(playPauseAction)
            .addAction(
                NotificationCompat.Action(
                    R.drawable.ic_skip_next,
                    "Next",
                    MediaSessionActionReceiver.getPlaybackActionPendingIntent(
                        this,
                        MediaSessionActionReceiver.ACTION_NEXT
                    )
                )
            )
            .addAction(
                NotificationCompat.Action(
                    R.drawable.ic_close,
                    "Stop",
                    MediaSessionActionReceiver.getPlaybackActionPendingIntent(
                        this,
                        MediaSessionActionReceiver.ACTION_STOP
                    )
                )
            )
            .build()
    }

    inner class MediaSessionCallback : MediaSession.Callback {
        @OptIn(androidx.media3.common.util.UnstableApi::class)
        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>
        ): ListenableFuture<MutableList<MediaItem>> {
            return Futures.immediateFuture(mediaItems)
        }

        override fun onSetMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>,
            startIndex: Int,
            startPositionMs: Long
        ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
            val itemsWithStartPosition = MediaSession.MediaItemsWithStartPosition(
                mediaItems,
                startIndex,
                startPositionMs
            )
            return Futures.immediateFuture(itemsWithStartPosition)
        }

        override fun onPlay(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo
        ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
            if (!exoPlayer.isPlaying) {
                resume()
            }
            return super.onPlay(mediaSession, controller)
        }

        override fun onPause(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo
        ): ListenableFuture<Void> {
            if (exoPlayer.isPlaying) {
                pause()
            }
            return Futures.immediateFuture(null)
        }

        override fun onStop(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo
        ): ListenableFuture<Void> {
            stop()
            return Futures.immediateFuture(null)
        }

        override fun onSeekTo(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            positionMs: Long
        ): ListenableFuture<Void> {
            seekTo(positionMs)
            return Futures.immediateFuture(null)
        }

        override fun onSkipToNextMediaItem(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo
        ): ListenableFuture<Void> {
            skipToNext()
            return Futures.immediateFuture(null)
        }

        override fun onSkipToPreviousMediaItem(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo
        ): ListenableFuture<Void> {
            skipToPrevious()
            return Futures.immediateFuture(null)
        }

        override fun onSetRepeatMode(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            repeatMode: Int
        ): ListenableFuture<Void> {
            setRepeatMode(repeatMode)
            return Futures.immediateFuture(null)
        }

        override fun onSetShuffleModeEnabled(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            shuffleModeEnabled: Boolean
        ): ListenableFuture<Void> {
            setShuffleModeEnabled(shuffleModeEnabled)
            return Futures.immediateFuture(null)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return super.onBind(intent)
    }
}