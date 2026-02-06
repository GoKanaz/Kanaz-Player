package com.gokanaz.kanazplayer.core.di

import android.app.Service
import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.google.common.util.concurrent.ListenableFuture
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import javax.inject.Qualifier

@Qualifier
annotation class ServicePlayer

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @Provides
    @ServiceScoped
    @ServicePlayer
    fun provideServiceExoPlayer(@ApplicationContext context: Context): ExoPlayer {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        return ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_LOCAL)
            .setPauseAtEndOfMediaItems(true)
            .setSeekBackIncrementMs(10000)
            .setSeekForwardIncrementMs(10000)
            .build()
    }

    @Provides
    @ServiceScoped
    fun provideAudioAttributes(): AudioAttributes {
        return AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()
    }

    @Provides
    @ServiceScoped
    fun provideServicePlayer(@ServicePlayer exoPlayer: ExoPlayer): Player {
        return exoPlayer
    }

    @Provides
    @ServiceScoped
    fun provideMediaSession(
        @ApplicationContext context: Context,
        @ServicePlayer exoPlayer: ExoPlayer
    ): MediaSession {
        return MediaSession.Builder(context, exoPlayer)
            .setCallback(MediaSession.Callback())
            .build()
    }

    @Provides
    @ServiceScoped
    fun provideMediaSessionFuture(
        mediaSession: MediaSession
    ): ListenableFuture<MediaSession> {
        return com.google.common.util.concurrent.Futures.immediateFuture(mediaSession)
    }
}