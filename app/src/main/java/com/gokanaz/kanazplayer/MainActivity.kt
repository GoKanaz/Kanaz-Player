package com.gokanaz.kanazplayer

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gokanaz.kanazplayer.data.model.Song
import com.gokanaz.kanazplayer.service.MusicPlaybackService
import com.gokanaz.kanazplayer.ui.home.HomeScreen
import com.gokanaz.kanazplayer.ui.library.LibraryScreen
import com.gokanaz.kanazplayer.ui.player.FullPlayerScreen
import com.gokanaz.kanazplayer.ui.player.MiniPlayer
import com.gokanaz.kanazplayer.ui.player.PlayerViewModel
import com.gokanaz.kanazplayer.ui.theme.KanazPlayerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val serviceIntent = Intent(this, MusicPlaybackService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
        
        setContent {
            val systemInDarkTheme = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemInDarkTheme) }
            
            KanazPlayerTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MusicPlayerApp(
                        isDarkTheme = isDarkTheme,
                        onThemeChange = { isDarkTheme = it }
                    )
                }
            }
        }
    }
}

enum class Screen {
    HOME, LIBRARY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicPlayerApp(
    viewModel: PlayerViewModel = viewModel(),
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var showFullPlayer by remember { mutableStateOf(false) }
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    
    Scaffold(
        bottomBar = {
            if (currentSong != null && !showFullPlayer) {
                MiniPlayer(
                    song = currentSong!!,
                    isPlaying = isPlaying,
                    onPlayPauseClick = { viewModel.togglePlayPause() },
                    onNextClick = { viewModel.playNext() },
                    onClick = { showFullPlayer = true }
                )
            }
        }
    ) { paddingValues ->
        when (currentScreen) {
            Screen.HOME -> {
                HomeScreen(
                    modifier = Modifier.padding(paddingValues),
                    viewModel = viewModel,
                    isDarkTheme = isDarkTheme,
                    onThemeChange = onThemeChange,
                    onNavigateToLibrary = { currentScreen = Screen.LIBRARY },
                    onExpandPlayer = { showFullPlayer = true }
                )
            }
            Screen.LIBRARY -> {
                LibraryScreen(
                    modifier = Modifier.padding(paddingValues),
                    viewModel = viewModel,
                    onBackClick = { currentScreen = Screen.HOME },
                    onSongClick = { song ->
                        viewModel.playSong(song)
                        currentScreen = Screen.HOME
                    }
                )
            }
        }
        
        AnimatedVisibility(
            visible = showFullPlayer,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            FullPlayerScreen(
                viewModel = viewModel,
                onCollapse = { showFullPlayer = false }
            )
        }
    }
}
