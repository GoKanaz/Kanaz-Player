package com.gokanaz.kanazplayer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gokanaz.kanazplayer.core.common.result.Result
import com.gokanaz.kanazplayer.feature.library.LibraryScreen
import com.gokanaz.kanazplayer.feature.library.LibraryViewModel
import com.gokanaz.kanazplayer.feature.player.PlayerScreen

sealed class Screen(val route: String) {
    object Library : Screen("library")
    object Player : Screen("player")
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Library.route
    ) {
        composable(route = Screen.Library.route) {
            val viewModel: LibraryViewModel = hiltViewModel()
            val songsResult by viewModel.songsResult.collectAsStateWithLifecycle()
            
            LibraryScreen(
                songsResult = songsResult,
                onSongClick = { song ->
                    viewModel.playSong(song)
                    navController.navigate(Screen.Player.route)
                },
                onPlayAll = { songs ->
                    viewModel.playAllSongs(songs)
                    navController.navigate(Screen.Player.route)
                },
                onRetry = { viewModel.loadSongs() }
            )
        }
        
        composable(route = Screen.Player.route) {
            PlayerScreen()
        }
    }
}