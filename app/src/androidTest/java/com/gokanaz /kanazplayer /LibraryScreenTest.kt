package com.gokanaz.kanazplayer

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.gokanaz.kanazplayer.core.common.result.Result
import com.gokanaz.kanazplayer.core.data.model.Song
import com.gokanaz.kanazplayer.feature.library.LibraryScreen
import org.junit.Rule
import org.junit.Test

class LibraryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testSongs = listOf(
        Song(
            id = 1L,
            contentUri = android.net.Uri.parse("content://media/external/audio/media/1"),
            path = "/storage/emulated/0/Music/song1.mp3",
            title = "Song One",
            artist = "Artist One",
            album = "Album One",
            duration = 180000L,
            size = 5000000L,
            albumId = 100L,
            trackNumber = 1,
            year = 2024,
            mimeType = "audio/mpeg",
            dateAdded = 1700000000000L,
            dateModified = 1700000000000L,
            bitrate = 320,
            sampleRate = 44100,
            channelCount = 2
        ),
        Song(
            id = 2L,
            contentUri = android.net.Uri.parse("content://media/external/audio/media/2"),
            path = "/storage/emulated/0/Music/song2.mp3",
            title = "Song Two",
            artist = "Artist Two",
            album = "Album Two",
            duration = 200000L,
            size = 6000000L,
            albumId = 200L,
            trackNumber = 1,
            year = 2024,
            mimeType = "audio/mpeg",
            dateAdded = 1700000001000L,
            dateModified = 1700000001000L,
            bitrate = 256,
            sampleRate = 48000,
            channelCount = 2
        )
    )

    @Test
    fun libraryScreen_shouldDisplaySongs_whenSuccessState() {
        var clickedSong: Song? = null

        composeTestRule.setContent {
            LibraryScreen(
                songsResult = Result.Success(testSongs),
                onSongClick = { song -> clickedSong = song },
                onPlayAll = { },
                onRetry = { }
            )
        }

        composeTestRule.onNodeWithText("Song One").assertIsDisplayed()
        composeTestRule.onNodeWithText("Artist One").assertIsDisplayed()
        composeTestRule.onNodeWithText("Album One").assertIsDisplayed()
        composeTestRule.onNodeWithText("Song Two").assertIsDisplayed()
        composeTestRule.onNodeWithText("Artist Two").assertIsDisplayed()
    }

    @Test
    fun libraryScreen_shouldCallOnSongClick_whenSongItemClicked() {
        var clickedSong: Song? = null

        composeTestRule.setContent {
            LibraryScreen(
                songsResult = Result.Success(listOf(testSongs[0])),
                onSongClick = { song -> clickedSong = song },
                onPlayAll = { },
                onRetry = { }
            )
        }

        composeTestRule.onNodeWithText("Song One").performClick()

        assert(clickedSong != null)
        assert(clickedSong?.id == 1L)
        assert(clickedSong?.title == "Song One")
        assert(clickedSong?.artist == "Artist One")
    }

    @Test
    fun libraryScreen_shouldDisplayLoading_whenLoadingState() {
        composeTestRule.setContent {
            LibraryScreen(
                songsResult = Result.Loading,
                onSongClick = { },
                onPlayAll = { },
                onRetry = { }
            )
        }

        composeTestRule.onNodeWithText("Loading…").assertIsDisplayed()
    }

    @Test
    fun libraryScreen_shouldDisplayError_whenErrorState() {
        composeTestRule.setContent {
            LibraryScreen(
                songsResult = Result.Error(Exception("Test error")),
                onSongClick = { },
                onPlayAll = { },
                onRetry = { }
            )
        }

        composeTestRule.onNodeWithText("Error loading songs").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test error").assertIsDisplayed()
    }

    @Test
    fun libraryScreen_shouldDisplayEmptyMessage_whenEmptyList() {
        composeTestRule.setContent {
            LibraryScreen(
                songsResult = Result.Success(emptyList()),
                onSongClick = { },
                onPlayAll = { },
                onRetry = { }
            )
        }

        composeTestRule.onNodeWithText("No songs found").assertIsDisplayed()
    }

    @Test
    fun libraryScreen_shouldDisplayLibraryTitle() {
        composeTestRule.setContent {
            LibraryScreen(
                songsResult = Result.Success(emptyList()),
                onSongClick = { },
                onPlayAll = { },
                onRetry = { }
            )
        }

        composeTestRule.onNodeWithText("Library").assertIsDisplayed()
    }

    @Test
    fun libraryScreen_shouldHandleMultipleSongClicks() {
        val clickedSongs = mutableListOf<Song>()

        composeTestRule.setContent {
            LibraryScreen(
                songsResult = Result.Success(testSongs),
                onSongClick = { song -> clickedSongs.add(song) },
                onPlayAll = { },
                onRetry = { }
            )
        }

        composeTestRule.onNodeWithText("Song One").performClick()
        composeTestRule.onNodeWithText("Song Two").performClick()

        assert(clickedSongs.size == 2)
        assert(clickedSongs[0].title == "Song One")
        assert(clickedSongs[1].title == "Song Two")
    }
}