package com.gokanaz.kanazplayer.feature.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gokanaz.kanazplayer.R
import com.gokanaz.kanazplayer.core.common.result.Result
import kotlin.math.floor

@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.initializeMediaController(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        when (playerState) {
            is Result.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is Result.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error loading player",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            is Result.Success -> {
                val state = (playerState as Result.Success).data
                PlayerContent(
                    state = state,
                    onPlayPause = { viewModel.togglePlayPause() },
                    onSeek = { viewModel.seekTo(it) },
                    onSkipNext = { viewModel.skipToNext() },
                    onSkipPrevious = { viewModel.skipToPrevious() },
                    onRepeatModeChange = { viewModel.setRepeatMode(it) },
                    onShuffleToggle = { viewModel.toggleShuffle() }
                )
            }
        }
    }
}

@Composable
private fun PlayerContent(
    state: PlayerViewModel.PlayerState,
    onPlayPause: () -> Unit,
    onSeek: (Long) -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onRepeatModeChange: (Int) -> Unit,
    onShuffleToggle: () -> Unit
) {
    var sliderPosition by remember { mutableStateOf(0f) }
    var isSeeking by remember { mutableStateOf(false) }

    LaunchedEffect(state.currentPosition, isSeeking) {
        if (!isSeeking && state.duration > 0) {
            sliderPosition = (state.currentPosition.toFloat() / state.duration.toFloat()) * 100f
        }
    }

    AlbumArtSection(
        song = state.currentSong,
        isBuffering = state.playbackState == Player.STATE_BUFFERING
    )

    Spacer(modifier = Modifier.height(32.dp))

    SongInfoSection(
        song = state.currentSong,
        isPlaying = state.isPlaying,
        playbackState = state.playbackState
    )

    Spacer(modifier = Modifier.height(24.dp))

    ProgressSection(
        currentPosition = state.currentPosition,
        duration = state.duration,
        sliderPosition = sliderPosition,
        bufferedPercentage = state.bufferedPercentage,
        onSeekStarted = { isSeeking = true },
        onSeekChanged = { newPosition ->
            sliderPosition = newPosition
        },
        onSeekCompleted = {
            isSeeking = false
            val newPosition = (sliderPosition / 100f) * state.duration
            onSeek(newPosition.toLong())
        }
    )

    Spacer(modifier = Modifier.height(32.dp))

    ControlButtonsSection(
        isPlaying = state.isPlaying,
        repeatMode = state.repeatMode,
        shuffleEnabled = state.shuffleModeEnabled,
        onPlayPause = onPlayPause,
        onSkipNext = onSkipNext,
        onSkipPrevious = onSkipPrevious,
        onRepeatModeChange = onRepeatModeChange,
        onShuffleToggle = onShuffleToggle
    )
}

@Composable
private fun AlbumArtSection(
    song: com.gokanaz.kanazplayer.core.data.model.Song?,
    isBuffering: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.Center
    ) {
        if (song != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(android.provider.MediaStore.Audio.Albums.getContentUri("external", song.albumId))
                    .placeholder(R.drawable.ic_music_note)
                    .error(R.drawable.ic_music_note)
                    .crossfade(true)
                    .build(),
                contentDescription = "Album Art",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "No Song",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (isBuffering) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clip(RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun SongInfoSection(
    song: com.gokanaz.kanazplayer.core.data.model.Song?,
    isPlaying: Boolean,
    playbackState: Int
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = song?.title ?: "No song selected",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = song?.artist ?: "Unknown Artist",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = song?.album ?: "Unknown Album",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(12.dp))

        when (playbackState) {
            Player.STATE_ENDED -> {
                Text(
                    text = "Playback ended",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Player.STATE_BUFFERING -> {
                Text(
                    text = "Buffering...",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            else -> {
                if (isPlaying) {
                    Text(
                        text = "Now Playing",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = "Paused",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressSection(
    currentPosition: Long,
    duration: Long,
    sliderPosition: Float,
    bufferedPercentage: Int,
    onSeekStarted: () -> Unit,
    onSeekChanged: (Float) -> Unit,
    onSeekCompleted: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Slider(
            value = sliderPosition,
            onValueChange = {
                onSeekChanged(it)
            },
            onValueChangeFinished = {
                onSeekCompleted()
            },
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            thumb = {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(currentPosition),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Text(
                text = formatTime(duration),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun ControlButtonsSection(
    isPlaying: Boolean,
    repeatMode: Int,
    shuffleEnabled: Boolean,
    onPlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onRepeatModeChange: (Int) -> Unit,
    onShuffleToggle: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    when (repeatMode) {
                        Player.REPEAT_MODE_OFF -> onRepeatModeChange(Player.REPEAT_MODE_ALL)
                        Player.REPEAT_MODE_ALL -> onRepeatModeChange(Player.REPEAT_MODE_ONE)
                        else -> onRepeatModeChange(Player.REPEAT_MODE_OFF)
                    }
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = when (repeatMode) {
                        Player.REPEAT_MODE_ONE -> Icons.Default.RepeatOne
                        else -> Icons.Default.Repeat
                    },
                    contentDescription = "Repeat Mode",
                    tint = if (repeatMode != Player.REPEAT_MODE_OFF) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    },
                    modifier = Modifier.size(24.dp)
                )
            }

            IconButton(
                onClick = onSkipPrevious,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(
                onClick = onPlayPause,
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(36.dp)
                )
            }

            IconButton(
                onClick = onSkipNext,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(
                onClick = onShuffleToggle,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = "Shuffle",
                    tint = if (shuffleEnabled) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    },
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

private fun formatTime(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = floor(totalSeconds / 60.0).toInt()
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}