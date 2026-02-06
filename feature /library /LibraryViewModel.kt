package com.gokanaz.kanazplayer.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gokanaz.kanazplayer.core.common.result.Result
import com.gokanaz.kanazplayer.core.data.model.Song
import com.gokanaz.kanazplayer.core.domain.usecase.GetSongsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getSongsUseCase: GetSongsUseCase
) : ViewModel() {

    private val _songsResult = MutableStateFlow<Result<List<Song>>>(Result.Loading)
    val songsResult: StateFlow<Result<List<Song>>> = _songsResult.asStateFlow()

    init {
        loadSongs()
    }

    fun loadSongs() {
        viewModelScope.launch {
            getSongsUseCase()
                .catch { error ->
                    _songsResult.value = Result.Error(error)
                }
                .onEach { result ->
                    _songsResult.value = result
                }
                .launchIn(viewModelScope)
        }
    }

    fun refreshSongs() {
        _songsResult.value = Result.Loading
        loadSongs()
    }

    fun playSong(song: Song) {
        viewModelScope.launch {
            
        }
    }

    fun playAllSongs(songs: List<Song>) {
        viewModelScope.launch {
            
        }
    }
}