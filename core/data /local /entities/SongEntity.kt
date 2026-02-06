package com.gokanaz.kanazplayer.core.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gokanaz.kanazplayer.core.data.model.Song
import android.net.Uri

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val uri: String,
    val artUri: String?,
    val duration: Long,
    val trackNumber: Int,
    val year: Int,
    val genre: String?,
    val composer: String?,
    val albumArtist: String?,
    val discNumber: Int,
    val mimeType: String,
    val size: Long,
    val dateAdded: Long,
    val dateModified: Long,
    val bitrate: Int,
    val sampleRate: Int,
    val channelCount: Int
)

fun SongEntity.toDomain(): Song {
    return Song(
        id = id,
        title = title,
        artist = artist,
        album = album,
        uri = Uri.parse(uri),
        artUri = artUri?.let { Uri.parse(it) },
        duration = duration,
        trackNumber = trackNumber,
        year = year,
        genre = genre,
        composer = composer,
        albumArtist = albumArtist,
        discNumber = discNumber,
        mimeType = mimeType,
        size = size,
        dateAdded = dateAdded,
        dateModified = dateModified,
        bitrate = bitrate,
        sampleRate = sampleRate,
        channelCount = channelCount
    )
}

fun Song.toEntity(): SongEntity {
    return SongEntity(
        id = id,
        title = title,
        artist = artist,
        album = album,
        uri = uri.toString(),
        artUri = artUri?.toString(),
        duration = duration,
        trackNumber = trackNumber,
        year = year,
        genre = genre,
        composer = composer,
        albumArtist = albumArtist,
        discNumber = discNumber,
        mimeType = mimeType,
        size = size,
        dateAdded = dateAdded,
        dateModified = dateModified,
        bitrate = bitrate,
        sampleRate = sampleRate,
        channelCount = channelCount
    )
}