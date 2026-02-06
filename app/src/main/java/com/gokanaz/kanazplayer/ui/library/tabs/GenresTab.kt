package com.gokanaz.kanazplayer.ui.library.tabs

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gokanaz.kanazplayer.ui.player.PlayerViewModel

@Composable
fun GenresTab(
    viewModel: PlayerViewModel,
    onGenreClick: (com.gokanaz.kanazplayer.data.repository.Genre) -> Unit = {}
) {
    val genres by viewModel.genres.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${genres.size} Genre",
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = { }) {
                Icon(Icons.Default.SwapVert, contentDescription = "Sort")
            }
        }
        
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(genres) { genre ->
                val color = getGenreColor(genre.name)
                ListItem(
                    headlineContent = {
                        Text(
                            text = genre.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    supportingContent = {
                        Text("${genre.songCount} lagu")
                    },
                    leadingContent = {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = color,
                                    shape = MaterialTheme.shapes.medium
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = genre.name.first().uppercase(),
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                        }
                    },
                    trailingContent = {
                        IconButton(onClick = { 
                            if (genre.songs.isNotEmpty()) {
                                viewModel.playSongs(genre.songs)
                            }
                        }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Play All")
                        }
                    },
                    modifier = Modifier.clickable { onGenreClick(genre) }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

fun getGenreColor(genreName: String): Color {
    return when (genreName) {
        "Heavy Metal" -> Color(0xFF6B4C9A)
        "Rock" -> Color(0xFF8B4C4C)
        "Pop" -> Color(0xFFE91E63)
        "Jazz" -> Color(0xFF3F51B5)
        "Classical" -> Color(0xFF795548)
        else -> Color(0xFF8B7355)
    }
}
