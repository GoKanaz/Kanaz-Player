package com.gokanaz.kanazplayer.data.model

data class LyricLine(
    val timeInMillis: Long,
    val text: String,
    val isActive: Boolean = false
)
