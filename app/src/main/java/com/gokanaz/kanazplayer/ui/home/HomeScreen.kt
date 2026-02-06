package com.gokanaz.kanazplayer.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
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
import androidx.core.content.ContextCompat
import com.gokanaz.kanazplayer.ui.library.tabs.*
import com.gokanaz.kanazplayer.ui.player.PlayerViewModel

enum class LibraryTab {
    SONGS, PLAYLISTS, FOLDERS, ALBUMS, ARTISTS, GENRES
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onNavigateToLibrary: () -> Unit,
    onExpandPlayer: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(LibraryTab.SONGS) }
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
    
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Kanaz Player") },
            navigationIcon = {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                }
                
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Settings") },
                        onClick = {
                            showMenu = false
                        },
                        leadingIcon = { Icon(Icons.Default.Settings, null) }
                    )
                    DropdownMenuItem(
                        text = { Text(if (isDarkTheme) "Light Mode" else "Dark Mode") },
                        onClick = {
                            onThemeChange(!isDarkTheme)
                            showMenu = false
                        },
                        leadingIcon = { 
                            Icon(
                                if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode, 
                                null
                            ) 
                        }
                    )
                }
            },
            actions = {
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More")
                }
            }
        )
        
        ScrollableTabRow(
            selectedTabIndex = selectedTab.ordinal,
            edgePadding = 0.dp
        ) {
            Tab(
                selected = selectedTab == LibraryTab.SONGS,
                onClick = { selectedTab = LibraryTab.SONGS },
                text = { Text("Lagu") }
            )
            Tab(
                selected = selectedTab == LibraryTab.PLAYLISTS,
                onClick = { selectedTab = LibraryTab.PLAYLISTS },
                text = { Text("Daftar putar") }
            )
            Tab(
                selected = selectedTab == LibraryTab.FOLDERS,
                onClick = { selectedTab = LibraryTab.FOLDERS },
                text = { Text("Folder") }
            )
            Tab(
                selected = selectedTab == LibraryTab.ALBUMS,
                onClick = { selectedTab = LibraryTab.ALBUMS },
                text = { Text("Album") }
            )
            Tab(
                selected = selectedTab == LibraryTab.ARTISTS,
                onClick = { selectedTab = LibraryTab.ARTISTS },
                text = { Text("Artis") }
            )
            Tab(
                selected = selectedTab == LibraryTab.GENRES,
                onClick = { selectedTab = LibraryTab.GENRES },
                text = { Text("Genre") }
            )
        }
        
        when (selectedTab) {
            LibraryTab.SONGS -> SongsTab(
                viewModel = viewModel, 
                onSongClick = { viewModel.playSong(it) }
            )
            LibraryTab.PLAYLISTS -> PlaylistsTab(viewModel = viewModel)
            LibraryTab.FOLDERS -> FoldersTab(viewModel = viewModel)
            LibraryTab.ALBUMS -> AlbumsTab(viewModel = viewModel)
            LibraryTab.ARTISTS -> ArtistsTab(viewModel = viewModel)
            LibraryTab.GENRES -> GenresTab(viewModel = viewModel)
        }
    }
}
