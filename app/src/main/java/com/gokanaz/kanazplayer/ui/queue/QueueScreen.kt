package com.gokanaz.kanazplayer.ui.queue

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gokanaz.kanazplayer.data.model.Song

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueScreen(
    queue: List<Song>,
    currentSong: Song?,
    isPlaying: Boolean,
    onSongClick: (Song) -> Unit,
    onRemoveFromQueue: (Int) -> Unit,
    onClearQueue: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Queue (${queue.size})") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (queue.isNotEmpty()) {
                        IconButton(onClick = onClearQueue) {
                            Icon(Icons.Default.ClearAll, contentDescription = "Clear Queue")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (queue.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.QueueMusic,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Queue is empty",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                itemsIndexed(queue) { index, song ->
                    QueueItem(
                        song = song,
                        index = index + 1,
                        isCurrentSong = song.id == currentSong?.id,
                        isPlaying = isPlaying && song.id == currentSong?.id,
                        onClick = { onSongClick(song) },
                        onRemove = { onRemoveFromQueue(index) }
                    )
                }
            }
        }
    }
}

@Composable
fun QueueItem(
    song: Song,
    index: Int,
    isCurrentSong: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                text = song.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (isCurrentSong) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isCurrentSong) FontWeight.Bold else FontWeight.Normal
            )
        },
        supportingContent = {
            Text(
                text = song.artist,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall
            )
        },
        leadingContent = {
            if (isCurrentSong && isPlaying) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = "Now Playing",
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = "$index",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        trailingContent = {
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Remove from queue",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        },
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(
                if (isCurrentSong) 
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                else 
                    MaterialTheme.colorScheme.surface
            )
    )
    HorizontalDivider()
}
