package com.gokanaz.kanazplayer.core.domain.usecase

import com.gokanaz.kanazplayer.core.common.result.Result
import com.gokanaz.kanazplayer.core.data.model.Song
import com.gokanaz.kanazplayer.core.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSongsUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    operator fun invoke(sortBy: SortOption = SortOption.TITLE_ASC): Flow<Result<List<Song>>> {
        return repository.songsFlow.map { songs ->
            try {
                val sortedSongs = when (sortBy) {
                    SortOption.TITLE_ASC -> songs.sortedBy { it.title }
                    SortOption.TITLE_DESC -> songs.sortedByDescending { it.title }
                    SortOption.ARTIST_ASC -> songs.sortedBy { it.artist }
                    SortOption.ARTIST_DESC -> songs.sortedByDescending { it.artist }
                    SortOption.ALBUM_ASC -> songs.sortedBy { it.album }
                    SortOption.ALBUM_DESC -> songs.sortedByDescending { it.album }
                    SortOption.DURATION_ASC -> songs.sortedBy { it.duration }
                    SortOption.DURATION_DESC -> songs.sortedByDescending { it.duration }
                    SortOption.DATE_ADDED_ASC -> songs.sortedBy { it.dateAdded }
                    SortOption.DATE_ADDED_DESC -> songs.sortedByDescending { it.dateAdded }
                }
                Result.Success(sortedSongs)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    enum class SortOption {
        TITLE_ASC,
        TITLE_DESC,
        ARTIST_ASC,
        ARTIST_DESC,
        ALBUM_ASC,
        ALBUM_DESC,
        DURATION_ASC,
        DURATION_DESC,
        DATE_ADDED_ASC,
        DATE_ADDED_DESC
    }
}