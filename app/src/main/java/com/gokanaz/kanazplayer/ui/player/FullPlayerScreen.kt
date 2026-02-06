package com.gokanaz.kanazplayer.ui.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gokanaz.kanazplayer.ui.equalizer.EqualizerDialog
import com.gokanaz.kanazplayer.ui.queue.QueueDialog
import com.gokanaz.kanazplayer.ui.sleep.SleepTimerDialog

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
    
    var showSleepTimer by remember { mutableStateOf(false) }
    var showEqualizer by remember { mutableStateOf(false) }
    var showQueue by remember { mutableStateOf(false) }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
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
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Lagu",
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (currentSong != null) {
                        Text(
                            text = currentSong!!.album,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                IconButton(onClick = { }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "More",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (albumArt != null) {
                    Image(
                        bitmap = albumArt!!.asImageBitmap(),
                        contentDescription = "Album Art",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        modifier = Modifier.size(120.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = currentSong?.title ?: "No Song",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currentSong?.artist ?: "Unknown Artist",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                val progress = if (duration > 0) {
                    (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
                } else 0f
                
                Slider(
                    value = progress,
                    onValueChange = { newValue ->
                        val newPosition = (newValue * duration).toLong()
                        viewModel.seekTo(newPosition)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        formatDuration(currentPosition),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        formatDuration(duration),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.toggleShuffle() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (isShuffleEnabled) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                IconButton(
                    onClick = { viewModel.playPrevious() },
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        modifier = Modifier.size(48.dp)
                    )
                }
                
                FilledIconButton(
                    onClick = { viewModel.togglePlayPause() },
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
                    onClick = { viewModel.playNext() },
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        modifier = Modifier.size(48.dp)
                    )
                }
                
                IconButton(
                    onClick = { viewModel.toggleRepeat() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.Repeat,
                        contentDescription = "Repeat",
                        tint = if (isRepeatEnabled) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(
                    onClick = { showSleepTimer = true },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = "Sleep Timer",
                        tint = if (sleepTimerActive) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(28.dp)
                    )
                }
                IconButton(
                    onClick = { showEqualizer = true },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.GraphicEq,
                        contentDescription = "Equalizer",
                        modifier = Modifier.size(28.dp)
                    )
                }
                IconButton(
                    onClick = { showQueue = true },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.QueueMusic,
                        contentDescription = "Queue",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            if (queue.size > 1) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
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
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Next song: ${queue.getOrNull(1)?.title ?: ""}",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
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
}
