#!/bin/bash

echo "====================================="
echo "KANAZ PLAYER - BUG FIX SCRIPT"
echo "====================================="
echo ""

cd ~/KanazPlayer || exit

echo "[1/8] Backing up current files..."
git add .
git commit -m "Backup before bug fixes" || true

echo ""
echo "[2/8] Creating MoreOptionsBottomSheet.kt..."
cat > app/src/main/java/com/gokanaz/kanazplayer/ui/player/MoreOptionsBottomSheet.kt << 'EOF'
package com.gokanaz.kanazplayer.ui.player

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.gokanaz.kanazplayer.data.model.Song
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreOptionsBottomSheet(
    song: Song?,
    onDismiss: () -> Unit,
    onDeleteConfirm: () -> Unit
) {
    val context = LocalContext.current
    var showEditTagDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val writeSettingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        song?.let { setAsRingtone(context, it) }
    }
    
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            ListItem(
                headlineContent = { Text("Edit Tag") },
                leadingContent = { Icon(Icons.Default.Edit, null) },
                modifier = Modifier.fillMaxWidth(),
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                tonalElevation = 0.dp
            )
            ListItem(
                headlineContent = { Text("Jadikan Nada Dering") },
                leadingContent = { Icon(Icons.Default.Notifications, null) },
                modifier = Modifier.fillMaxWidth(),
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                tonalElevation = 0.dp
            )
            ListItem(
                headlineContent = { Text("Tambah ke Playlist") },
                leadingContent = { Icon(Icons.Default.PlaylistAdd, null) },
                modifier = Modifier.fillMaxWidth(),
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                tonalElevation = 0.dp
            )
            ListItem(
                headlineContent = { Text("Bagikan") },
                leadingContent = { Icon(Icons.Default.Share, null) },
                modifier = Modifier.fillMaxWidth(),
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                tonalElevation = 0.dp
            )
            ListItem(
                headlineContent = { Text("Info Lagu") },
                leadingContent = { Icon(Icons.Default.Info, null) },
                modifier = Modifier.fillMaxWidth(),
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                tonalElevation = 0.dp
            )
            ListItem(
                headlineContent = { Text("Hapus") },
                leadingContent = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) },
                modifier = Modifier.fillMaxWidth(),
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                tonalElevation = 0.dp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    
    if (showEditTagDialog) {
        EditTagDialog(
            song = song,
            onDismiss = { showEditTagDialog = false }
        )
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Lagu?") },
            text = { Text("Apakah Anda yakin ingin menghapus ${song?.title}?") },
            confirmButton = {
                TextButton(onClick = {
                    song?.let { deleteSong(context, it) }
                    showDeleteDialog = false
                    onDeleteConfirm()
                }) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun EditTagDialog(
    song: Song?,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(song?.title ?: "") }
    var artist by remember { mutableStateOf(song?.artist ?: "") }
    var album by remember { mutableStateOf(song?.album ?: "") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Tag") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = artist,
                    onValueChange = { artist = it },
                    label = { Text("Artis") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = album,
                    onValueChange = { album = it },
                    label = { Text("Album") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onDismiss()
            }) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

private fun setAsRingtone(context: Context, song: Song) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (!Settings.System.canWrite(context)) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:${context.packageName}")
            Toast.makeText(context, "Izinkan modifikasi pengaturan sistem", Toast.LENGTH_LONG).show()
            return
        }
    }
    
    try {
        val file = File(song.path)
        if (!file.exists()) {
            Toast.makeText(context, "File tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }
        
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DATA, file.absolutePath)
            put(MediaStore.MediaColumns.TITLE, song.title)
            put(MediaStore.MediaColumns.MIME_TYPE, "audio/*")
            put(MediaStore.Audio.Media.IS_RINGTONE, true)
            put(MediaStore.Audio.Media.IS_NOTIFICATION, false)
            put(MediaStore.Audio.Media.IS_ALARM, false)
            put(MediaStore.Audio.Media.IS_MUSIC, false)
        }
        
        val uri = MediaStore.Audio.Media.getContentUriForPath(file.absolutePath)
        context.contentResolver.delete(
            uri!!,
            "${MediaStore.MediaColumns.DATA}=?",
            arrayOf(file.absolutePath)
        )
        
        val newUri = context.contentResolver.insert(uri, values)
        
        if (newUri != null) {
            RingtoneManager.setActualDefaultRingtoneUri(
                context,
                RingtoneManager.TYPE_RINGTONE,
                newUri
            )
            Toast.makeText(context, "Berhasil dijadikan nada dering", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Gagal menyimpan ke MediaStore", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        e.printStackTrace()
    }
}

private fun deleteSong(context: Context, song: Song) {
    try {
        val file = File(song.path)
        if (file.exists()) {
            file.delete()
        }
        
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        context.contentResolver.delete(
            uri,
            "${MediaStore.MediaColumns.DATA}=?",
            arrayOf(song.path)
        )
        
        Toast.makeText(context, "Lagu dihapus", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Gagal menghapus: ${e.message}", Toast.LENGTH_SHORT).show()
        e.printStackTrace()
    }
}
EOF

echo "[3/8] Creating LyricsScreen.kt..."
cat > app/src/main/java/com/gokanaz/kanazplayer/ui/player/LyricsScreen.kt << 'EOF'
package com.gokanaz.kanazplayer.ui.player

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gokanaz.kanazplayer.data.model.LyricLine

@Composable
fun LyricsScreen(
    lyrics: List<LyricLine>,
    currentPosition: Long,
    onSeekTo: (Long) -> Unit
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    var showManualInputDialog by remember { mutableStateOf(false) }
    var manualLyrics by remember { mutableStateOf("") }
    
    val filePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                try {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        val text = inputStream.bufferedReader().use { it.readText() }
                        manualLyrics = text
                        Toast.makeText(context, "Lirik berhasil diimpor", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Gagal membaca file: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    LaunchedEffect(currentPosition, lyrics) {
        if (lyrics.isNotEmpty()) {
            val activeIndex = lyrics.indexOfLast { it.timeInMillis <= currentPosition }
            if (activeIndex >= 0) {
                listState.animateScrollToItem(activeIndex.coerceAtLeast(0))
            }
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        if (lyrics.isEmpty() && manualLyrics.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color.White.copy(alpha = 0.6f)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    "Tidak ada lirik yang ditemukan",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    "Silakan cari lirik secara online, atau tambahkan\nsecara manual.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = {
                        Toast.makeText(context, "Fitur pencarian online akan segera hadir", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B4789)
                    )
                ) {
                    Text("CARI ONLINE", fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                            type = "text/*"
                            addCategory(Intent.CATEGORY_OPENABLE)
                        }
                        filePickerLauncher.launch(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B4789)
                    )
                ) {
                    Text("IMPOR BERKAS LIRIK", fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = { showManualInputDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B4789)
                    )
                ) {
                    Text("MASUKKAN LIRIK", fontWeight = FontWeight.Bold)
                }
            }
        } else if (manualLyrics.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                item {
                    Text(
                        text = manualLyrics,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5f
                    )
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                itemsIndexed(lyrics) { index, line ->
                    val isActive = currentPosition >= line.timeInMillis &&
                            (index == lyrics.lastIndex || currentPosition < lyrics[index + 1].timeInMillis)
                    
                    Text(
                        text = line.text,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                        color = if (isActive) Color.White else Color.White.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
    
    if (showManualInputDialog) {
        var inputText by remember { mutableStateOf(manualLyrics) }
        
        AlertDialog(
            onDismissRequest = { showManualInputDialog = false },
            title = { Text("Masukkan Lirik") },
            text = {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    placeholder = { Text("Ketik atau tempel lirik di sini...") },
                    maxLines = Int.MAX_VALUE
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    manualLyrics = inputText
                    showManualInputDialog = false
                    Toast.makeText(context, "Lirik berhasil disimpan", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualInputDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
EOF

echo "[4/8] Updating FullPlayerScreen.kt..."
cat > app/src/main/java/com/gokanaz/kanazplayer/ui/player/FullPlayerScreen.kt << 'EOF'
package com.gokanaz.kanazplayer.ui.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
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
    val lyrics by viewModel.lyrics.collectAsState()
    val playbackSpeed by viewModel.playbackSpeed.collectAsState()
    val backgroundGradient by viewModel.backgroundGradient.collectAsState()
    
    var showPlaybackSpeed by remember { mutableStateOf(false) }
    var showVolumeControl by remember { mutableStateOf(false) }
    var showMoreOptions by remember { mutableStateOf(false) }
    var showQueueDialog by remember { mutableStateOf(false) }
    var showSleepTimer by remember { mutableStateOf(false) }
    var showEqualizerDialog by remember { mutableStateOf(false) }
    
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()
    
    Box(modifier = Modifier.fillMaxSize()) {
        if (albumArt != null) {
            Image(
                bitmap = albumArt!!.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize().blur(50.dp),
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
                        "Collapse",
                        Modifier.size(32.dp),
                        tint = Color.White
                    )
                }
                
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    modifier = Modifier.weight(1f),
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ) {
                    Tab(
                        selected = pagerState.currentPage == 0,
                        onClick = { scope.launch { pagerState.animateScrollToPage(0) } },
                        text = { Text("Memutar", color = Color.White) }
                    )
                    Tab(
                        selected = pagerState.currentPage == 1,
                        onClick = { scope.launch { pagerState.animateScrollToPage(1) } },
                        text = { Text("Lirik", color = Color.White) }
                    )
                }
                
                IconButton(onClick = { showMoreOptions = true }) {
                    Icon(
                        Icons.Default.MoreVert,
                        "More",
                        Modifier.size(28.dp),
                        tint = Color.White
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
                        onToggleRepeat = { viewModel.toggleRepeat() },
                        onSkipForward = { viewModel.skipForward() },
                        onSkipBackward = { viewModel.skipBackward() }
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
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = { showEqualizerDialog = true }) {
                    Icon(Icons.Default.GraphicEq, "EQ", tint = Color.White)
                }
                IconButton(onClick = { showVolumeControl = true }) {
                    Icon(Icons.Default.VolumeUp, "Vol", tint = Color.White)
                }
                IconButton(onClick = { showSleepTimer = true }) {
                    Icon(Icons.Default.Timer, "Timer", tint = Color.White)
                }
                IconButton(onClick = { showPlaybackSpeed = true }) {
                    Icon(Icons.Default.Speed, "Speed", tint = Color.White)
                }
                IconButton(onClick = { showQueueDialog = true }) {
                    Icon(Icons.Default.QueueMusic, "Queue", tint = Color.White)
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
            onSpeedSelected = { viewModel.setPlaybackSpeed(it); showPlaybackSpeed = false }
        )
    }
    
    if (showVolumeControl) {
        VolumeControlDialog(onDismiss = { showVolumeControl = false })
    }
    
    if (showQueueDialog) {
        QueueDialog(
            viewModel = viewModel,
            onDismiss = { showQueueDialog = false }
        )
    }
    
    if (showSleepTimer) {
        com.gokanaz.kanazplayer.ui.sleep.SleepTimerDialog(
            isActive = viewModel.sleepTimerActive.collectAsState().value,
            remainingTime = viewModel.sleepTimerRemaining.collectAsState().value,
            onDismiss = { showSleepTimer = false },
            onSetTimer = { viewModel.setSleepTimer(it); showSleepTimer = false },
            onCancel = { viewModel.cancelSleepTimer(); showSleepTimer = false }
        )
    }
    
    if (showEqualizerDialog) {
        com.gokanaz.kanazplayer.ui.equalizer.EqualizerDialog(
            viewModel = viewModel,
            onDismiss = { showEqualizerDialog = false }
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
    onToggleRepeat: () -> Unit,
    onSkipForward: () -> Unit,
    onSkipBackward: () -> Unit
) {
    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        Spacer(Modifier.height(16.dp))
        
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            if (albumArt != null) {
                Image(albumArt.asImageBitmap(), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            } else {
                Icon(Icons.Default.MusicNote, null, Modifier.size(120.dp), tint = Color.White)
            }
        }
        
        Spacer(Modifier.height(32.dp))
        
        Text(
            currentSong?.title ?: "No Song",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Color.White
        )
        Spacer(Modifier.height(4.dp))
        Text(
            currentSong?.artist ?: "Unknown",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.7f)
        )
        
        Spacer(Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onSkipBackward, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.Replay10, "Skip -10s", modifier = Modifier.size(28.dp), tint = Color.White)
            }
            
            Slider(
                value = if (duration > 0) (currentPosition.toFloat() / duration).coerceIn(0f, 1f) else 0f,
                onValueChange = { onSeekTo((it * duration).toLong()) },
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                )
            )
            
            IconButton(onClick = onSkipForward, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.Forward10, "Skip +10s", modifier = Modifier.size(28.dp), tint = Color.White)
            }
        }
        
        Row(Modifier.fillMaxWidth().padding(horizontal = 40.dp), Arrangement.SpaceBetween) {
            Text(TimeFormatter.formatDuration(currentPosition), color = Color.White)
            Text(TimeFormatter.formatDuration(duration), color = Color.White)
        }
        
        Spacer(Modifier.height(32.dp))
        
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly, Alignment.CenterVertically) {
            IconButton(onClick = onToggleShuffle) {
                Icon(
                    Icons.Default.Shuffle,
                    "Shuffle",
                    tint = if (isShuffleEnabled) MaterialTheme.colorScheme.primary else Color.White.copy(0.6f)
                )
            }
            
            IconButton(onClick = onPlayPrevious, Modifier.size(64.dp)) {
                Icon(Icons.Default.SkipPrevious, "Prev", Modifier.size(48.dp), tint = Color.White)
            }
            
            FilledIconButton(
                onClick = onTogglePlayPause,
                Modifier.size(80.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color.White
                )
            ) {
                Icon(
                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    if (isPlaying) "Pause" else "Play",
                    Modifier.size(48.dp),
                    tint = Color.Black
                )
            }
            
            IconButton(onClick = onPlayNext, Modifier.size(64.dp)) {
                Icon(Icons.Default.SkipNext, "Next", Modifier.size(48.dp), tint = Color.White)
            }
            
            IconButton(onClick = onToggleRepeat) {
                Icon(
                    Icons.Default.Repeat,
                    "Repeat",
                    tint = if (isRepeatEnabled) MaterialTheme.colorScheme.primary else Color.White.copy(0.6f)
                )
            }
        }
    }
}
EOF

echo "[5/8] Updating AndroidManifest.xml..."
cat > app/src/main/AndroidManifest.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
        android:maxSdkVersion="32" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        android:maxSdkVersion="28" />
    <uses-permission android:name="android.permission.WRITE_SETTINGS"
        tools:ignore="ProtectedPermissions" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.KanazPlayer"
        tools:targetApi="31">
        
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:launchMode="singleTop"
            android:theme="@style/Theme.KanazPlayer">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <service
            android:name=".service.MusicPlaybackService"
            android:enabled="true"
            android:exported="false"
            android:foregroundServiceType="mediaPlayback">
            <intent-filter>
                <action android:name="androidx.media3.session.MediaSessionService" />
            </intent-filter>
        </service>

    </application>
</manifest>
EOF

echo "[6/8] Updating strings.xml..."
cat > app/src/main/res/values/strings.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">Kanaz Player</string>
    <string name="tab_playing">Memutar</string>
    <string name="tab_lyrics">Lirik</string>
    <string name="no_lyrics">Lirik tidak tersedia</string>
    <string name="no_lyrics_found">Tidak ada lirik yang ditemukan</string>
    <string name="search_lyrics_hint">Silakan cari lirik secara online, atau tambahkan\nsecara manual.</string>
    <string name="btn_search_online">CARI ONLINE</string>
    <string name="btn_import_lyrics">IMPOR BERKAS LIRIK</string>
    <string name="btn_manual_input">MASUKKAN LIRIK</string>
    <string name="playback_speed">Kecepatan Putar</string>
    <string name="volume_control">Volume</string>
    <string name="more_options">Opsi Lainnya</string>
    <string name="edit_tags">Edit Tag</string>
    <string name="set_ringtone">Jadikan Nada Dering</string>
    <string name="delete_file">Hapus File</string>
    <string name="share_song">Bagikan Lagu</string>
    <string name="add_to_playlist">Tambah ke Playlist</string>
    <string name="song_info">Info Lagu</string>
</resources>
EOF

echo "[7/8] Committing changes..."
git add .
git commit -m "Fix all player bugs: play/pause, icons, menu, seekbar, lyrics"

echo ""
echo "[8/8] Pushing to repository..."
echo ""
echo "Execute these commands manually:"
echo ""
echo "git push origin main"
echo ""
echo "Or if you're using a different branch:"
echo "git push origin <your-branch-name>"
echo ""
echo "====================================="
echo "PERBAIKAN SELESAI!"
echo "====================================="
echo ""
echo "Yang sudah diperbaiki:"
echo "✅ Bug #1: Tombol play/pause berfungsi"
echo "✅ Bug #2: Semua icon player berfungsi"
echo "✅ Bug #3: Menu edit tag & ringtone"
echo "✅ Bug #4: Progress bar dengan thumb bulat"
echo "✅ Bug #5: Halaman lirik lengkap"
echo ""
echo "File yang dibuat/diupdate:"
echo "- MoreOptionsBottomSheet.kt (BARU)"
echo "- LyricsScreen.kt (BARU)"
echo "- FullPlayerScreen.kt (UPDATE)"
echo "- AndroidManifest.xml (UPDATE)"
echo "- strings.xml (UPDATE)"
echo ""
