package com.gokanaz.kanazplayer.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gokanaz.kanazplayer.core.data.local.entities.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {

    @Query("SELECT * FROM songs ORDER BY dateAdded DESC")
    fun getSongs(): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE id = :songId")
    suspend fun getSongById(songId: Long): SongEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<SongEntity>)

    @Query("DELETE FROM songs")
    suspend fun deleteAllSongs()

    @Query("SELECT * FROM songs WHERE title LIKE :query OR artist LIKE :query OR album LIKE :query")
    fun searchSongs(query: String): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE albumId = :albumId ORDER BY trackNumber")
    fun getSongsByAlbum(albumId: Long): Flow<List<SongEntity>>

    @Query("SELECT DISTINCT artist FROM songs ORDER BY artist")
    fun getArtists(): Flow<List<String>>

    @Query("SELECT DISTINCT album FROM songs ORDER BY album")
    fun getAlbums(): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM songs")
    suspend fun getSongCount(): Int
}