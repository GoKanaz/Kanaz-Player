package com.gokanaz.kanazplayer.core.di

import android.content.Context
import androidx.room.Room
import com.gokanaz.kanazplayer.core.data.local.KanazDatabase
import com.gokanaz.kanazplayer.core.data.local.dao.SongDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    @Singleton
    fun provideKanazDatabase(@ApplicationContext context: Context): KanazDatabase {
        return Room.databaseBuilder(
            context,
            KanazDatabase::class.java,
            "kanaz_database.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideSongDao(database: KanazDatabase): SongDao {
        return database.songDao()
    }
}