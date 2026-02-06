package com.gokanaz.kanazplayer.ui.player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gokanaz.kanazplayer.data.model.LyricLine
import com.gokanaz.kanazplayer.ui.theme.LyricActiveColor
import com.gokanaz.kanazplayer.ui.theme.LyricInactiveColor
import kotlinx.coroutines.launch

@Composable
fun LyricsScreen(
    lyrics: List<LyricLine>,
    currentPosition: Long,
    onSeekTo: (Long) -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    val currentIndex = remember(currentPosition, lyrics) {
        lyrics.indexOfLast { it.timeInMillis <= currentPosition }
    }
    
    LaunchedEffect(currentIndex) {
        if (currentIndex >= 0 && lyrics.isNotEmpty()) {
            val targetIndex = (currentIndex - 1).coerceAtLeast(0)
            coroutineScope.launch {
                listState.animateScrollToItem(targetIndex)
            }
        }
    }
    
    if (lyrics.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Lirik tidak tersedia",
                style = MaterialTheme.typography.bodyLarge,
                color = LyricInactiveColor
            )
        }
    } else {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 200.dp, horizontal = 24.dp)
        ) {
            itemsIndexed(lyrics) { index, lyric ->
                val isActive = index == currentIndex
                
                Text(
                    text = lyric.text,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSeekTo(lyric.timeInMillis) }
                        .padding(vertical = 12.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = if (isActive) 20.sp else 16.sp,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (isActive) LyricActiveColor else LyricInactiveColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
