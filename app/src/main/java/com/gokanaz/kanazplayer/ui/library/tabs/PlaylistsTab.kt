package com.gokanaz.kanazplayer.ui.library.tabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gokanaz.kanazplayer.ui.player.PlayerViewModel

@Composable
fun PlaylistsTab(viewModel: PlayerViewModel) {
    val playlists by viewModel.playlists.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${playlists.size} Daftar putar",
                style = MaterialTheme.typography.titleMedium
            )
            Row {
                IconButton(onClick = { showCreateDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More")
                }
            }
        }
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            item {
                PlaylistCard(
                    icon = Icons.Default.Favorite,
                    title = "Favorit Saya",
                    subtitle = "0 lagu",
                    onClick = { }
                )
            }
            
            item {
                PlaylistCard(
                    icon = Icons.Default.AccessTime,
                    title = "Terakhir Ditambahkan",
                    subtitle = "${playlists.size} lagu",
                    onClick = { }
                )
            }
            
            item {
                PlaylistCard(
                    icon = Icons.Default.Album,
                    title = "Baru-baru ini Dimainkan",
                    subtitle = "0 lagu",
                    onClick = { }
                )
            }
            
            item {
                PlaylistCard(
                    icon = Icons.Default.Whatshot,
                    title = "Lagu Teratas Saya",
                    subtitle = "0 lagu",
                    onClick = { }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Daftar Putar Saya (${playlists.size})",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            items(playlists) { playlist ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = playlist.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    supportingContent = {
                        Text("${playlist.songIds.size} lagu")
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.PlaylistPlay,
                            contentDescription = null
                        )
                    },
                    trailingContent = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }
                    },
                    modifier = Modifier.clickable { }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
    
    if (showCreateDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name ->
                viewModel.createPlaylist(name)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun PlaylistCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CreatePlaylistDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var playlistName by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Buat daftar putar") },
        text = {
            TextField(
                value = playlistName,
                onValueChange = { playlistName = it },
                label = { Text("Nama daftar putar") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (playlistName.isNotBlank()) {
                        onCreate(playlistName.trim())
                    }
                },
                enabled = playlistName.isNotBlank()
            ) {
                Text("Buat")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
