package com.gokanaz.kanazplayer.data.model

data class Playlist(
    val id: Long,
    val name: String,
    val songIds: List<Long>
)
