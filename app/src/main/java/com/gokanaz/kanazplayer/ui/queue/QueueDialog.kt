package com.gokanaz.kanazplayer.ui.queue

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gokanaz.kanazplayer.data.model.Song

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueDialog(
    queue: List<Song>,
    currentSong: Song?,
    onDismiss: () -> Unit,
    onSongClick: (Song) -> Unit,
    onRemove: (Int) -> Unit,
    onClear: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Queue (${queue.size})",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Row {
                        TextButton(onClick = onClear) {
                            Text("Clear")
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }
                
                Divider()
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(queue) { index, song ->
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = song.title,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = if (song == currentSong) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }
                                )
                            },
                            supportingContent = {
                                Text(
                                    text = song.artist,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            leadingContent = {
                                if (song == currentSong) {
                                    Icon(
                                        Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                } else {
                                    Text("${index + 1}")
                                }
                            },
                            trailingContent = {
                                if (index > 0) {
                                    IconButton(onClick = { onRemove(index) }) {
                                        Icon(Icons.Default.Close, contentDescription = "Remove")
                                    }
                                }
                            },
                            modifier = Modifier.clickable { onSongClick(song) }
                        )
                    }
                }
            }
        }
    }
}
