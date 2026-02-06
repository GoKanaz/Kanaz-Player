package com.gokanaz.kanazplayer.ui.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gokanaz.kanazplayer.ui.equalizer.EqualizerDialog
import com.gokanaz.kanazplayer.ui.queue.QueueDialog
import com.gokanaz.kanazplayer.ui.sleep.SleepTimerDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullPlayerScreen(
    viewModel: PlayerViewModel,
    onCollapse: () -> Unit
) {
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val isShuffleEnabled by viewModel.isShuffleEnabled.collectAsState()
    val isRepeatEnabled by viewModel.isRepeatEnabled.collectAsState()
    val albumArt by viewModel.albumArt.collectAsState()
    val queue by viewModel.queue.collectAsState()
    val sleepTimerActive by viewModel.sleepTimerActive.collectAsState()
    val sleepTimerRemaining by viewModel.sleepTimerRemaining.collectAsState()
    val lyrics by viewModel.lyrics.collectAsState()
    val playbackSpeed by viewModel.playbackSpeed.collectAsState()
    val backgroundGradient by viewModel.backgroundGradient.collectAsState()
    
    var showSleepTimer by remember { mutableStateOf(false) }
    var showEqualizer by remember { mutableStateOf(false) }
    var showQueue by remember { mutableStateOf(false) }
    var showMoreOptions by remember { mutableStateOf(false) }
    var showPlaybackSpeed by remember { mutableStateOf(false) }
    var showVolumeControl by remember { mutableStateOf(false) }
    
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()
    
    Box(modifier = Modifier.fillMaxSize()) {
        if (albumArt != null) {
            Image(
                bitmap = albumArt!!.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(50.dp),
                contentScale = ContentScale.Crop
            )
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            backgroundGradient.first.copy(alpha = 0.9f),
                            backgroundGradient.second
                        )
                    )
                )
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onCollapse) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Collapse",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    modifier = Modifier.weight(1f),
                    containerColor = androidx.compose.ui.graphics.Color.Transparent,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                ) {
                    Tab(
                        selected = pagerState.currentPage == 0,
                        onClick = { scope.launch { pagerState.animateScrollToPage(0) } },
                        text = {
                            Text(
                                "Memutar",
                                color = if (pagerState.currentPage == 0)
                                    MaterialTheme.colorScheme.onSurface
                                else
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    )
                    Tab(
                        selected = pagerState.currentPage == 1,
                        onClick = { scope.launch { pagerState.animateScrollToPage(1) } },
                        text = {
                            Text(
                                "Lirik",
                                color = if (pagerState.currentPage == 1)
                                    MaterialTheme.colorScheme.onSurface
                                else
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    )
                }
                
                IconButton(onClick = { showMoreOptions = true }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "More",
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                when (page) {
                    0 -> PlayingPage(
                        albumArt = albumArt,
                        currentSong = currentSong,
                        currentPosition = currentPosition,
                        duration = duration,
                        isPlaying = isPlaying,
                        isShuffleEnabled = isShuffleEnabled,
                        isRepeatEnabled = isRepeatEnabled,
                        queue = queue,
                        onSeekTo = { viewModel.seekTo(it) },
                        onTogglePlayPause = { viewModel.togglePlayPause() },
                        onPlayNext = { viewModel.playNext() },
                        onPlayPrevious = { viewModel.playPrevious() },
                        onToggleShuffle = { viewModel.toggleShuffle() },
                        onToggleRepeat = { viewModel.toggleRepeat() }
                    )
                    1 -> LyricsScreen(
                        lyrics = lyrics,
                        currentPosition = currentPosition,
                        onSeekTo = { viewModel.seekTo(it) }
                    )
                }
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showEqualizer = true }) {
                    Icon(
                        Icons.Default.GraphicEq,
                        contentDescription = "Equalizer",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                IconButton(onClick = { showVolumeControl = true }) {
                    Icon(
                        Icons.Default.VolumeUp,
                        contentDescription = "Volume",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                IconButton(onClick = { showSleepTimer = true }) {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = "Sleep Timer",
                        tint = if (sleepTimerActive) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.onSurface
                    )
                }
                
                IconButton(onClick = { showPlaybackSpeed = true }) {
                    Icon(
                        Icons.Default.Speed,
                        contentDescription = "Speed",
                        tint = if (playbackSpeed != 1.0f) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.onSurface
                    )
                }
                
                IconButton(onClick = { showQueue = true }) {
                    Icon(
                        Icons.Default.QueueMusic,
                        contentDescription = "Queue",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
    
    if (showSleepTimer) {
        SleepTimerDialog(
            isActive = sleepTimerActive,
            remainingTime = sleepTimerRemaining,
            onDismiss = { showSleepTimer = false },
            onSetTimer = { minutes ->
                viewModel.setSleepTimer(minutes)
                showSleepTimer = false
            },
            onCancel = {
                viewModel.cancelSleepTimer()
                showSleepTimer = false
            }
        )
    }
    
    if (showEqualizer) {
        EqualizerDialog(
            viewModel = viewModel,
            onDismiss = { showEqualizer = false }
        )
    }
    
    if (showQueue) {
        QueueDialog(
            queue = queue,
            currentSong = currentSong,
            onDismiss = { showQueue = false },
            onSongClick = { song ->
                viewModel.playSong(song)
                showQueue = false
            },
            onRemove = { index ->
                viewModel.removeFromQueue(index)
            },
            onClear = {
                viewModel.clearQueue()
            }
        )
    }
    
    if (showMoreOptions) {
        MoreOptionsBottomSheet(
            song = currentSong,
            onDismiss = { showMoreOptions = false },
            onDeleteConfirm = {
                onCollapse()
            }
        )
    }
    
    if (showPlaybackSpeed) {
        PlaybackSpeedDialog(
            currentSpeed = playbackSpeed,
            onDismiss = { showPlaybackSpeed = false },
            onSpeedSelected = { speed ->
                viewModel.setPlaybackSpeed(speed)
                showPlaybackSpeed = false
            }
        )
    }
    
    if (showVolumeControl) {
        VolumeControlDialog(
            onDismiss = { showVolumeControl = false }
        )
    }
}

@Composable
private fun PlayingPage(
    albumArt: android.graphics.Bitmap?,
    currentSong: com.gokanaz.kanazplayer.data.model.Song?,
    currentPosition: Long,
    duration: Long,
    isPlaying: Boolean,
    isShuffleEnabled: Boolean,
    isRepeatEnabled: Boolean,
    queue: List<com.gokanaz.kanazplayer.data.model.Song>,
    onSeekTo: (Long) -> Unit,
    onTogglePlayPause: () -> Unit,
    onPlayNext: () -> Unit,
    onPlayPrevious: () -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            if (albumArt != null) {
                Image(
                    bitmap = albumArt.asImageBitmap(),
                    contentDescription = "Album Art",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = currentSong?.title ?: "No Song",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = currentSong?.artist ?: "Unknown Artist",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        val progress = if (duration > 0) {
            (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
        } else 0f
        
        Slider(
            value = progress,
            onValueChange = { newValue ->
                val newPosition = (newValue * duration).toLong()
                onSeekTo(newPosition)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary
            )
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                formatDuration(currentPosition),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                formatDuration(duration),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onToggleShuffle,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Default.Shuffle,
                    contentDescription = "Shuffle",
                    tint = if (isShuffleEnabled) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    },
                    modifier = Modifier.size(28.dp)
                )
            }
            
            IconButton(
                onClick = onPlayPrevious,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            
            FilledIconButton(
                onClick = onTogglePlayPause,
                modifier = Modifier.size(80.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    modifier = Modifier.size(48.dp)
                )
            }
            
            IconButton(
                onClick = onPlayNext,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            
            IconButton(
                onClick = onToggleRepeat,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Default.Repeat,
                    contentDescription = "Repeat",
                    tint = if (isRepeatEnabled) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    },
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        if (queue.size > 1) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.MusicNote,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Next: ${queue.getOrNull(1)?.title ?: ""}",
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

fun formatDuration(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
