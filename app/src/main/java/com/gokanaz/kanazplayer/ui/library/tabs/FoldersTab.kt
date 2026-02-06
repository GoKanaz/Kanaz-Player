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
fun FoldersTab(
    viewModel: PlayerViewModel,
    onFolderClick: (com.gokanaz.kanazplayer.data.repository.MusicFolder) -> Unit = {}
) {
    val folders by viewModel.folders.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${folders.size} Folder",
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = { }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More")
            }
        }
        
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(folders) { folder ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = folder.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    supportingContent = {
                        Text("${folder.songCount} lagu")
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                    },
                    trailingContent = {
                        IconButton(onClick = { 
                            if (folder.songs.isNotEmpty()) {
                                viewModel.playSongs(folder.songs)
                            }
                        }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Play All")
                        }
                    },
                    modifier = Modifier.clickable { onFolderClick(folder) }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
