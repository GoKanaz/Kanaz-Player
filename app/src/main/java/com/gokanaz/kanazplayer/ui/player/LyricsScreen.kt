package com.gokanaz.kanazplayer.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gokanaz.kanazplayer.data.model.LyricLine
import com.gokanaz.kanazplayer.util.TimeFormatter
import kotlinx.coroutines.launch

@Composable
fun LyricsScreen(
    lyrics: List<LyricLine>,
    currentPosition: Long,
    onSeekTo: (Long) -> Unit
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    
    val currentLineIndex = remember(currentPosition, lyrics) {
        lyrics.indexOfLast { it.timestamp <= currentPosition }.coerceAtLeast(0)
    }
    
    LaunchedEffect(currentLineIndex) {
        if (lyrics.isNotEmpty() && currentLineIndex >= 0) {
            scope.launch {
                listState.animateScrollToItem(
                    index = currentLineIndex,
                    scrollOffset = -200
                )
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (lyrics.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No lyrics available",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onSeekTo((currentPosition - 10000).coerceAtLeast(0)) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Replay10,
                        "Skip -10s",
                        modifier = Modifier.size(28.dp),
                        tint = Color.White
                    )
                }
                
                Text(
                    TimeFormatter.formatDuration(currentPosition),
                    color = Color.White,
                    fontSize = 16.sp
                )
                
                IconButton(
                    onClick = { onSeekTo(currentPosition + 10000) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.Default.Forward10,
                        "Skip +10s",
                        modifier = Modifier.size(28.dp),
                        tint = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 200.dp)
            ) {
                itemsIndexed(lyrics) { index, lyric ->
                    val isActive = index == currentLineIndex
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSeekTo(lyric.timestamp) }
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .then(
                                if (isActive) {
                                    Modifier.background(
                                        Color.White.copy(alpha = 0.1f),
                                        RoundedCornerShape(8.dp)
                                    )
                                } else Modifier
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = TimeFormatter.formatDuration(lyric.timestamp),
                            color = if (isActive) Color.White else Color.White.copy(alpha = 0.5f),
                            fontSize = 12.sp,
                            modifier = Modifier.width(50.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Text(
                            text = lyric.text,
                            color = if (isActive) Color.White else Color.White.copy(alpha = 0.6f),
                            fontSize = if (isActive) 20.sp else 16.sp,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
