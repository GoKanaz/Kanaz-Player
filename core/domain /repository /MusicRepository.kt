package com.gokanaz.kanazplayer.core.domain.repository

import com.gokanaz.kanazplayer.core.common.result.Result
import com.gokanaz.kanazplayer.core.data.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MusicRepository {
    val songsFlow: StateFlow<List<Song>>
    
    suspend fun fetchSongs(): Flow<List<Song>>
    
    suspend fun getSongById(id: Long): Song?
}