package com.gokanaz.kanazplayer.core.extensions

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.gokanaz.kanazplayer.core.data.model.Song

fun Cursor.toSong(contentResolver: ContentResolver): Song? {
    return try {
        val id = getLong(getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
        val contentUri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            id
        )
        Song(
            id = id,
            contentUri = contentUri,
            path = getString(getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)) ?: "",
            title = getString(getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)) ?: "",
            artist = getString(getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)) ?: "",
            album = getString(getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)) ?: "",
            duration = getLong(getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)),
            size = getLong(getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)),
            albumId = getLong(getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)),
            trackNumber = getInt(getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)),
            year = getInt(getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)),
            mimeType = getString(getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)) ?: "",
            dateAdded = getLong(getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)),
            dateModified = getLong(getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)),
            bitrate = 0,
            sampleRate = 0,
            channelCount = 0
        )
    } catch (e: Exception) {
        null
    }
}

fun Long.formatDuration(): String {
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

fun Uri.toSongId(): Long? {
    return try {
        ContentUris.parseId(this)
    } catch (e: Exception) {
        null
    }
}