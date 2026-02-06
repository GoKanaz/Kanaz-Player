package com.gokanaz.kanazplayer.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gokanaz.kanazplayer.core.data.local.dao.SongDao
import com.gokanaz.kanazplayer.core.data.local.entities.SongEntity

@Database(
    entities = [SongEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(UriConverter::class)
abstract class KanazDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
}