package com.gokanaz.kanazplayer.core.data.model

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MediaMetadata.PICTURE_TYPE_FRONT_COVER

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val uri: Uri,
    val artUri: Uri?,
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
) {
    fun toMediaItem(): MediaItem {
        return MediaItem.Builder()
            .setMediaId(id.toString())
            .setUri(uri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .setAlbumTitle(album)
                    .setAlbumArtist(albumArtist)
                    .setComposer(composer)
                    .setGenre(genre)
                    .setTrackNumber(trackNumber)
                    .setDiscNumber(discNumber)
                    .setYear(year)
                    .setDurationMillis(duration)
                    .setArtworkUri(artUri)
                    .build()
            )
            .build()
    }

    companion object {
        fun fromMediaItem(mediaItem: MediaItem, id: Long = mediaItem.mediaId.toLongOrNull() ?: 0L): Song {
            val metadata = mediaItem.mediaMetadata
            return Song(
                id = id,
                title = metadata.title?.toString() ?: "",
                artist = metadata.artist?.toString() ?: "",
                album = metadata.albumTitle?.toString() ?: "",
                uri = mediaItem.requestMetadata.mediaUri ?: Uri.EMPTY,
                artUri = metadata.artworkUri,
                duration = metadata.durationMillis ?: 0L,
                trackNumber = metadata.trackNumber ?: -1,
                year = metadata.year ?: -1,
                genre = metadata.genre?.toString(),
                composer = metadata.composer?.toString(),
                albumArtist = metadata.albumArtist?.toString(),
                discNumber = metadata.discNumber ?: 1,
                mimeType = "",
                size = 0L,
                dateAdded = 0L,
                dateModified = 0L,
                bitrate = 0,
                sampleRate = 0,
                channelCount = 0
            )
        }
    }
}

fun Song.toMediaMetadata(): MediaMetadata {
    return MediaMetadata.Builder()
        .setTitle(title)
        .setArtist(artist)
        .setAlbumTitle(album)
        .setAlbumArtist(albumArtist)
        .setComposer(composer)
        .setGenre(genre)
        .setTrackNumber(trackNumber)
        .setDiscNumber(discNumber)
        .setYear(year)
        .setDurationMillis(duration)
        .setArtworkUri(artUri)
        .build()
}

fun List<Song>.toMediaItems(): List<MediaItem> {
    return this.map { it.toMediaItem() }
}

fun Song.copyWithNewArtUri(newArtUri: Uri?): Song {
    return this.copy(artUri = newArtUri)
}

val Song.isValid: Boolean
    get() = id > 0 && duration > 0 && uri != Uri.EMPTY

val Song.formattedDuration: String
    get() {
        val totalSeconds = duration / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }