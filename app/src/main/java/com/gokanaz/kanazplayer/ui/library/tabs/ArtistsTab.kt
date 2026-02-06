package com.gokanaz.kanazplayer.ui.library.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gokanaz.kanazplayer.ui.player.PlayerViewModel

@Composable
fun ArtistsTab(
    viewModel: PlayerViewModel,
    onArtistClick: (com.gokanaz.kanazplayer.data.repository.Artist) -> Unit = {}
) {
    val artists by viewModel.artists.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${artists.size} Artis",
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = { }) {
                Icon(Icons.Default.SwapVert, contentDescription = "Sort")
            }
        }
        
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(artists) { artist ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = artist.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    supportingContent = {
                        Text("${artist.albumCount} album | ${artist.songCount} lagu")
                    },
                    leadingContent = {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = artist.name.firstOrNull()?.uppercase() ?: "?",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    },
                    trailingContent = {
                        IconButton(onClick = { 
                            if (artist.songs.isNotEmpty()) {
                                viewModel.playSongs(artist.songs)
                            }
                        }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Play All")
                        }
                    },
                    modifier = Modifier.clickable { onArtistClick(artist) }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
