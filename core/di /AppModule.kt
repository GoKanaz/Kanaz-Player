package com.gokanaz.kanazplayer.core.di

import android.content.ContentResolver
import android.content.Context
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.gokanaz.kanazplayer.core.data.repository.MusicRepository
import com.gokanaz.kanazplayer.core.data.repository.MusicRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver {
        return context.contentResolver
    }

    @Provides
    @Singleton
    fun provideMusicRepository(contentResolver: ContentResolver): MusicRepository {
        return MusicRepositoryImpl(contentResolver)
    }

    @Provides
    @Singleton
    fun provideExoPlayer(@ApplicationContext context: Context): ExoPlayer {
        return ExoPlayer.Builder(context)
            .setSeekBackIncrementMs(10000)
            .setSeekForwardIncrementMs(10000)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(androidx.media3.common.C.WAKE_MODE_LOCAL)
            .setPauseAtEndOfMediaItems(true)
            .build()
    }

    @Provides
    @Singleton
    fun providePlayer(exoPlayer: ExoPlayer): Player {
        return exoPlayer
    }
}