package com.gokanaz.kanazplayer.data.model

import android.graphics.Bitmap

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val path: String,
    val albumArtUri: String? = null,
    var albumArtBitmap: Bitmap? = null
)
