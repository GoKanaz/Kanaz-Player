package com.gokanaz.kanazplayer.core.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.gokanaz.kanazplayer.core.data.model.Song
import com.gokanaz.kanazplayer.core.domain.repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepositoryImpl @Inject constructor(
    private val contentResolver: ContentResolver
) : MusicRepository {

    private val _songsFlow = MutableStateFlow<List<Song>>(emptyList())
    override val songsFlow: StateFlow<List<Song>> = _songsFlow.asStateFlow()

    private val contentObserver = object : ContentObserver(null) {
        override fun onChange(selfChange: Boolean) {
            loadSongs()
        }
    }

    init {
        contentResolver.registerContentObserver(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            true,
            contentObserver
        )
        loadSongs()
    }

    private fun loadSongs() {
        CoroutineScope(Dispatchers.IO).launch {
            val songs = fetchSongsFromMediaStore()
            _songsFlow.value = songs
        }
    }

    override suspend fun fetchSongs(): Flow<List<Song>> = flow {
        val songs = fetchSongsFromMediaStore()
        emit(songs)
    }.flowOn(Dispatchers.IO)

    private suspend fun fetchSongsFromMediaStore(): List<Song> {
        return try {
            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.MIME_TYPE,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.DATE_MODIFIED,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) MediaStore.Audio.Media.BITRATE else null,
                MediaStore.Audio.Media.SAMPLE_RATE,
                MediaStore.Audio.Media.CHANNEL_COUNT
            ).filterNotNull().toTypedArray()

            val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} >= 60000"
            val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"

            val cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder
            )

            val songs = mutableListOf<Song>()
            cursor?.use { c ->
                val idColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val pathColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val titleColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val sizeColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                val albumIdColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val trackColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
                val yearColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
                val mimeTypeColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)
                val dateAddedColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                val dateModifiedColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)
                val bitrateColumn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) c.getColumnIndexOrThrow(MediaStore.Audio.Media.BITRATE) else -1
                val sampleRateColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.SAMPLE_RATE)
                val channelCountColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.CHANNEL_COUNT)

                while (c.moveToNext()) {
                    val id = c.getLong(idColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    val song = Song(
                        id = id,
                        contentUri = contentUri,
                        path = c.getString(pathColumn) ?: "",
                        title = c.getString(titleColumn) ?: "",
                        artist = c.getString(artistColumn) ?: "",
                        album = c.getString(albumColumn) ?: "",
                        duration = c.getLong(durationColumn),
                        size = c.getLong(sizeColumn),
                        albumId = c.getLong(albumIdColumn),
                        trackNumber = c.getInt(trackColumn),
                        year = c.getInt(yearColumn),
                        mimeType = c.getString(mimeTypeColumn) ?: "",
                        dateAdded = c.getLong(dateAddedColumn),
                        dateModified = c.getLong(dateModifiedColumn),
                        bitrate = if (bitrateColumn != -1) c.getInt(bitrateColumn) else 0,
                        sampleRate = c.getInt(sampleRateColumn),
                        channelCount = c.getInt(channelCountColumn)
                    )
                    songs.add(song)
                }
            }
            songs
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getSongById(id: Long): Song? {
        return try {
            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA
            )
            val selection = "${MediaStore.Audio.Media._ID} = ?"
            val selectionArgs = arrayOf(id.toString())
            val cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
            )
            cursor?.use { c ->
                if (c.moveToFirst()) {
                    val idColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                    val pathColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                    val songId = c.getLong(idColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        songId
                    )
                    return Song(
                        id = songId,
                        contentUri = contentUri,
                        path = c.getString(pathColumn) ?: "",
                        title = "",
                        artist = "",
                        album = "",
                        duration = 0L,
                        size = 0L,
                        albumId = 0L,
                        trackNumber = 0,
                        year = 0,
                        mimeType = "",
                        dateAdded = 0L,
                        dateModified = 0L,
                        bitrate = 0,
                        sampleRate = 0,
                        channelCount = 0
                    )
                }
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun cleanup() {
        contentResolver.unregisterContentObserver(contentObserver)
    }
}