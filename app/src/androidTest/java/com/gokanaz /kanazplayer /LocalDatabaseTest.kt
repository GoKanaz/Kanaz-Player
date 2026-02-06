package com.gokanaz.kanazplayer

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gokanaz.kanazplayer.core.data.local.KanazDatabase
import com.gokanaz.kanazplayer.core.data.local.entities.SongEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalDatabaseTest {

    private lateinit var database: KanazDatabase
    private lateinit var songDao: com.gokanaz.kanazplayer.core.data.local.dao.SongDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            KanazDatabase::class.java
        ).allowMainThreadQueries().build()
        songDao = database.songDao()
    }

    @After
    fun cleanup() {
        database.close()
    }

    @Test
    fun insertSongs_and_getSongs_shouldReturnCorrectData() = runBlocking {
        val testSong = SongEntity(
            id = 1L,
            title = "Test Song",
            artist = "Test Artist",
            album = "Test Album",
            uri = "content://media/external/audio/media/1",
            artUri = "content://media/external/audio/albumart/1",
            duration = 180000L,
            trackNumber = 1,
            year = 2024,
            genre = "Pop",
            composer = "Composer",
            albumArtist = "Album Artist",
            discNumber = 1,
            mimeType = "audio/mpeg",
            size = 5000000L,
            dateAdded = 1700000000000L,
            dateModified = 1700000000000L,
            bitrate = 320,
            sampleRate = 44100,
            channelCount = 2
        )

        songDao.insertSongs(listOf(testSong))
        val songs = songDao.getSongs().first()

        assertEquals(1, songs.size)
        assertEquals(testSong.id, songs[0].id)
        assertEquals(testSong.title, songs[0].title)
        assertEquals(testSong.artist, songs[0].artist)
        assertEquals(testSong.album, songs[0].album)
        assertEquals(testSong.uri, songs[0].uri)
        assertEquals(testSong.artUri, songs[0].artUri)
        assertEquals(testSong.duration, songs[0].duration)
    }

    @Test
    fun getSongById_shouldReturnCorrectSong() = runBlocking {
        val testSong = SongEntity(
            id = 2L,
            title = "Specific Song",
            artist = "Specific Artist",
            album = "Specific Album",
            uri = "content://media/external/audio/media/2",
            artUri = null,
            duration = 200000L,
            trackNumber = 1,
            year = 2024,
            genre = "Rock",
            composer = null,
            albumArtist = null,
            discNumber = 1,
            mimeType = "audio/mpeg",
            size = 6000000L,
            dateAdded = 1700000001000L,
            dateModified = 1700000001000L,
            bitrate = 256,
            sampleRate = 48000,
            channelCount = 2
        )

        songDao.insertSongs(listOf(testSong))
        val retrievedSong = songDao.getSongById(2L)

        assertEquals(testSong.id, retrievedSong?.id)
        assertEquals(testSong.title, retrievedSong?.title)
        assertEquals(testSong.artist, retrievedSong?.artist)
        assertEquals(testSong.duration, retrievedSong?.duration)
    }

    @Test
    fun deleteAllSongs_shouldEmptyDatabase() = runBlocking {
        val testSong = SongEntity(
            id = 3L,
            title = "Delete Song",
            artist = "Delete Artist",
            album = "Delete Album",
            uri = "content://media/external/audio/media/3",
            artUri = null,
            duration = 150000L,
            trackNumber = 1,
            year = 2024,
            genre = null,
            composer = null,
            albumArtist = null,
            discNumber = 1,
            mimeType = "audio/mpeg",
            size = 3000000L,
            dateAdded = 1700000002000L,
            dateModified = 1700000002000L,
            bitrate = 192,
            sampleRate = 44100,
            channelCount = 2
        )

        songDao.insertSongs(listOf(testSong))
        songDao.deleteAllSongs()
        val songs = songDao.getSongs().first()

        assertEquals(0, songs.size)
    }

    @Test
    fun searchSongs_shouldReturnMatchingResults() = runBlocking {
        val songs = listOf(
            SongEntity(
                id = 4L,
                title = "Beautiful Day",
                artist = "U2",
                album = "All That You Can't Leave Behind",
                uri = "content://media/external/audio/media/4",
                artUri = null,
                duration = 240000L,
                trackNumber = 1,
                year = 2000,
                genre = "Rock",
                composer = null,
                albumArtist = "U2",
                discNumber = 1,
                mimeType = "audio/mpeg",
                size = 8000000L,
                dateAdded = 1700000003000L,
                dateModified = 1700000003000L,
                bitrate = 320,
                sampleRate = 44100,
                channelCount = 2
            ),
            SongEntity(
                id = 5L,
                title = "Day Dreaming",
                artist = "Radiohead",
                album = "The Bends",
                uri = "content://media/external/audio/media/5",
                artUri = null,
                duration = 220000L,
                trackNumber = 2,
                year = 1995,
                genre = "Alternative",
                composer = null,
                albumArtist = "Radiohead",
                discNumber = 1,
                mimeType = "audio/mpeg",
                size = 7000000L,
                dateAdded = 1700000004000L,
                dateModified = 1700000004000L,
                bitrate = 320,
                sampleRate = 44100,
                channelCount = 2
            )
        )

        songDao.insertSongs(songs)
        val searchResults = songDao.searchSongs("Day").first()

        assertEquals(2, searchResults.size)
        assertEquals("Beautiful Day", searchResults[0].title)
        assertEquals("Day Dreaming", searchResults[1].title)
    }

    @Test
    fun getSongCount_shouldReturnCorrectCount() = runBlocking {
        val songs = listOf(
            SongEntity(
                id = 6L,
                title = "Song 1",
                artist = "Artist 1",
                album = "Album 1",
                uri = "content://media/external/audio/media/6",
                artUri = null,
                duration = 180000L,
                trackNumber = 1,
                year = 2024,
                genre = null,
                composer = null,
                albumArtist = null,
                discNumber = 1,
                mimeType = "audio/mpeg",
                size = 5000000L,
                dateAdded = 1700000005000L,
                dateModified = 1700000005000L,
                bitrate = 320,
                sampleRate = 44100,
                channelCount = 2
            ),
            SongEntity(
                id = 7L,
                title = "Song 2",
                artist = "Artist 2",
                album = "Album 2",
                uri = "content://media/external/audio/media/7",
                artUri = null,
                duration = 200000L,
                trackNumber = 1,
                year = 2024,
                genre = null,
                composer = null,
                albumArtist = null,
                discNumber = 1,
                mimeType = "audio/mpeg",
                size = 6000000L,
                dateAdded = 1700000006000L,
                dateModified = 1700000006000L,
                bitrate = 320,
                sampleRate = 44100,
                channelCount = 2
            )
        )

        songDao.insertSongs(songs)
        val count = songDao.getSongCount()

        assertEquals(2, count)
    }
}