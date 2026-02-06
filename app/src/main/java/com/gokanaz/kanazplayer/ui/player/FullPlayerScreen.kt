package com.gokanaz.kanazplayer.ui.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
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
import com.gokanaz.kanazplayer.util.TimeFormatter
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
    val lyrics by viewModel.lyrics.collectAsState()
    val playbackSpeed by viewModel.playbackSpeed.collectAsState()
    val backgroundGradient by viewModel.backgroundGradient.collectAsState()
    
    var showPlaybackSpeed by remember { mutableStateOf(false) }
    var showVolumeControl by remember { mutableStateOf(false) }
    var showMoreOptions by remember { mutableStateOf(false) }
    
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
                    containerColor = androidx.compose.ui.graphics.Color.Transparent
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
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Equalizer */ }) {
                    Icon(Icons.Default.GraphicEq, "Equalizer")
                }
                
                IconButton(onClick = { showVolumeControl = true }) {
                    Icon(Icons.Default.VolumeUp, "Volume")
                }
                
                IconButton(onClick = { /* Timer */ }) {
                    Icon(Icons.Default.Timer, "Timer")
                }
                
                IconButton(onClick = { showPlaybackSpeed = true }) {
                    Icon(Icons.Default.Speed, "Speed")
                }
                
                IconButton(onClick = { /* Queue */ }) {
                    Icon(Icons.Default.QueueMusic, "Queue")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
    
    if (showMoreOptions) {
        MoreOptionsBottomSheet(
            song = currentSong,
            onDismiss = { showMoreOptions = false },
            onDeleteConfirm = { onCollapse() }
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
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(TimeFormatter.formatDuration(currentPosition))
            Text(TimeFormatter.formatDuration(duration))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggleShuffle) {
                Icon(
                    Icons.Default.Shuffle,
                    "Shuffle",
                    tint = if (isShuffleEnabled) MaterialTheme.colorScheme.primary
                           else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            IconButton(onClick = onPlayPrevious, modifier = Modifier.size(64.dp)) {
                Icon(Icons.Default.SkipPrevious, "Previous", modifier = Modifier.size(48.dp))
            }
            
            FilledIconButton(
                onClick = onTogglePlayPause,
                modifier = Modifier.size(80.dp),
                shape = CircleShape
            ) {
                Icon(
                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    "Play/Pause",
                    modifier = Modifier.size(48.dp)
                )
            }
            
            IconButton(onClick = onPlayNext, modifier = Modifier.size(64.dp)) {
                Icon(Icons.Default.SkipNext, "Next", modifier = Modifier.size(48.dp))
            }
            
            IconButton(onClick = onToggleRepeat) {
                Icon(
                    Icons.Default.Repeat,
                    "Repeat",
                    tint = if (isRepeatEnabled) MaterialTheme.colorScheme.primary
                           else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}
