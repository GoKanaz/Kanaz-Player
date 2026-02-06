package com.gokanaz.kanazplayer.ui.player

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gokanaz.kanazplayer.ui.sleep.SleepTimerDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel = viewModel(),
    onLibraryClick: () -> Unit = {},
    onQueueClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onPlaylistsClick: () -> Unit = {},
    onEqualizerClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val songs by viewModel.songs.collectAsState()
    val isShuffleEnabled by viewModel.isShuffleEnabled.collectAsState()
    val isRepeatEnabled by viewModel.isRepeatEnabled.collectAsState()
    val albumArt by viewModel.albumArt.collectAsState()
    val sleepTimerActive by viewModel.sleepTimerActive.collectAsState()
    val sleepTimerRemaining by viewModel.sleepTimerRemaining.collectAsState()
    
    var showSleepTimerDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    
    var hasPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_MEDIA_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            }
        )
    }
    
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            viewModel.loadSongs()
        }
    }
    
    LaunchedEffect(Unit) {
        if (!hasPermission) {
            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_AUDIO
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
            launcher.launch(permission)
        } else {
            viewModel.loadSongs()
        }
    }
    
    LaunchedEffect(currentPosition, duration) {
        if (duration > 0 && currentPosition >= duration - 500 && isPlaying) {
            viewModel.playNext()
        }
    }
    
    if (showSleepTimerDialog) {
        SleepTimerDialog(
            isActive = sleepTimerActive,
            remainingTime = sleepTimerRemaining,
            onDismiss = { showSleepTimerDialog = false },
            onSetTimer = { minutes ->
                viewModel.setSleepTimer(minutes)
                showSleepTimerDialog = false
            },
            onCancel = {
                viewModel.cancelSleepTimer()
                showSleepTimerDialog = false
            }
        )
    }
    
    if (!hasPermission) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Storage permission required",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "We need permission to read your music files",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Manifest.permission.READ_MEDIA_AUDIO
                    } else {
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    }
                    launcher.launch(permission)
                }) {
                    Text("Grant Permission")
                }
            }
        }
        return
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Now Playing") },
                navigationIcon = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                    
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Library") },
                            onClick = {
                                showMenu = false
                                onLibraryClick()
                            },
                            leadingIcon = { Icon(Icons.Default.LibraryMusic, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Playlists") },
                            onClick = {
                                showMenu = false
                                onPlaylistsClick()
                            },
                            leadingIcon = { Icon(Icons.Default.PlaylistPlay, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Queue") },
                            onClick = {
                                showMenu = false
                                onQueueClick()
                            },
                            leadingIcon = { Icon(Icons.Default.QueueMusic, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Equalizer") },
                            onClick = {
                                showMenu = false
                                onEqualizerClick()
                            },
                            leadingIcon = { Icon(Icons.Default.GraphicEq, null) }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = {
                                showMenu = false
                                onSettingsClick()
                            },
                            leadingIcon = { Icon(Icons.Default.Settings, null) }
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showSleepTimerDialog = true }) {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = "Sleep Timer",
                            tint = if (sleepTimerActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Text("${songs.size}")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
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
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currentSong?.title ?: "No Song",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(0.9f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currentSong?.artist ?: "Unknown Artist",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(0.9f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(modifier = Modifier.fillMaxWidth()) {
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
                    Text(formatDuration(currentPosition), style = MaterialTheme.typography.bodySmall)
                    Text(formatDuration(duration), style = MaterialTheme.typography.bodySmall)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = { viewModel.toggleShuffle() }) {
                    Icon(
                        Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (isShuffleEnabled) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
                IconButton(onClick = { viewModel.toggleRepeat() }) {
                    Icon(
                        Icons.Default.Repeat,
                        contentDescription = "Repeat",
                        tint = if (isRepeatEnabled) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
                IconButton(onClick = onEqualizerClick) {
                    Icon(
                        Icons.Default.GraphicEq,
                        contentDescription = "Equalizer",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onQueueClick) {
                    Icon(
                        Icons.Default.QueueMusic,
                        contentDescription = "Queue",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.playPrevious() },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        modifier = Modifier.size(36.dp)
                    )
                }
                
                Surface(
                    modifier = Modifier.size(72.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.primary,
                    onClick = { viewModel.togglePlayPause() }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                
                IconButton(
                    onClick = { viewModel.playNext() },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

fun formatDuration(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / (1000 * 60)) % 60
    return String.format("%d:%02d", minutes, seconds)
}
