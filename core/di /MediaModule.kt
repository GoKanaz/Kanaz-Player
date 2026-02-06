package com.gokanaz.kanazplayer.core.di

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MediaModule {

    @Provides
    @Singleton
    fun provideSessionToken(@ApplicationContext context: Context): SessionToken {
        return SessionToken(context, SessionToken.TYPE_SESSION_SERVICE)
    }

    @Provides
    @Singleton
    fun provideMediaControllerFuture(
        @ApplicationContext context: Context,
        sessionToken: SessionToken
    ): ListenableFuture<MediaController> {
        return MediaController.Builder(context, sessionToken).buildAsync()
    }

    @Provides
    @Singleton
    fun provideMediaController(
        mediaControllerFuture: ListenableFuture<MediaController>
    ): MediaController {
        return mediaControllerFuture.get()
    }

    @Provides
    @Singleton
    fun providePlayer(mediaController: MediaController): Player {
        return mediaController
    }
}