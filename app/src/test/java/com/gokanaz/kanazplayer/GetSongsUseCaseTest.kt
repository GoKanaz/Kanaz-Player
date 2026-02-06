package com.gokanaz.kanazplayer

import com.gokanaz.kanazplayer.core.common.result.Result
import com.gokanaz.kanazplayer.core.data.model.Song
import com.gokanaz.kanazplayer.core.domain.repository.MusicRepository
import com.gokanaz.kanazplayer.core.domain.usecase.GetSongsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetSongsUseCaseTest {

    private val mockRepository = mockk<MusicRepository>()
    private val getSongsUseCase = GetSongsUseCase(mockRepository)

    private val testSongs = listOf(
        Song(
            id = 1L,
            contentUri = android.net.Uri.parse("content://media/external/audio/media/1"),
            path = "/storage/emulated/0/Music/test1.mp3",
            title = "Test Song 1",
            artist = "Test Artist",
            album = "Test Album",
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
            path = "/storage/emulated/0/Music/test2.mp3",
            title = "Test Song 2",
            artist = "Test Artist",
            album = "Test Album",
            duration = 200000L,
            size = 6000000L,
            albumId = 100L,
            trackNumber = 2,
            year = 2024,
            mimeType = "audio/mpeg",
            dateAdded = 1700000001000L,
            dateModified = 1700000001000L,
            bitrate = 320,
            sampleRate = 44100,
            channelCount = 2
        )
    )

    @Test
    fun `invoke should return sorted songs when repository returns songs`() = runTest {
        coEvery { mockRepository.songsFlow } returns flowOf(testSongs)

        val resultFlow = getSongsUseCase()
        val results = mutableListOf<Result<List<Song>>>()
        resultFlow.collect { results.add(it) }

        assertEquals(1, results.size)
        assert(results[0] is Result.Success)
        val successResult = results[0] as Result.Success
        assertEquals(2, successResult.data.size)
        assertEquals("Test Song 1", successResult.data[0].title)
        assertEquals("Test Song 2", successResult.data[1].title)
    }

    @Test
    fun `invoke should return error when repository throws exception`() = runTest {
        val exception = RuntimeException("Database error")
        coEvery { mockRepository.songsFlow } returns flow { throw exception }

        val resultFlow = getSongsUseCase()
        val results = mutableListOf<Result<List<Song>>>()
        resultFlow.collect { results.add(it) }

        assertEquals(1, results.size)
        assert(results[0] is Result.Error)
        val errorResult = results[0] as Result.Error
        assertEquals(exception, errorResult.exception)
    }

    @Test
    fun `invoke should sort songs by title ascending when sort option is TITLE_ASC`() = runTest {
        val unsortedSongs = listOf(
            testSongs[1].copy(title = "Banana"),
            testSongs[0].copy(title = "Apple")
        )
        coEvery { mockRepository.songsFlow } returns flowOf(unsortedSongs)

        val resultFlow = getSongsUseCase(GetSongsUseCase.SortOption.TITLE_ASC)
        val results = mutableListOf<Result<List<Song>>>()
        resultFlow.collect { results.add(it) }

        assertEquals(1, results.size)
        val successResult = results[0] as Result.Success
        assertEquals("Apple", successResult.data[0].title)
        assertEquals("Banana", successResult.data[1].title)
    }

    @Test
    fun `invoke should sort songs by title descending when sort option is TITLE_DESC`() = runTest {
        val unsortedSongs = listOf(
            testSongs[0].copy(title = "Apple"),
            testSongs[1].copy(title = "Banana")
        )
        coEvery { mockRepository.songsFlow } returns flowOf(unsortedSongs)

        val resultFlow = getSongsUseCase(GetSongsUseCase.SortOption.TITLE_DESC)
        val results = mutableListOf<Result<List<Song>>>()
        resultFlow.collect { results.add(it) }

        assertEquals(1, results.size)
        val successResult = results[0] as Result.Success
        assertEquals("Banana", successResult.data[0].title)
        assertEquals("Apple", successResult.data[1].title)
    }

    @Test
    fun `invoke should return empty list when repository returns empty list`() = runTest {
        coEvery { mockRepository.songsFlow } returns flowOf(emptyList())

        val resultFlow = getSongsUseCase()
        val results = mutableListOf<Result<List<Song>>>()
        resultFlow.collect { results.add(it) }

        assertEquals(1, results.size)
        val successResult = results[0] as Result.Success
        assertEquals(0, successResult.data.size)
    }
}