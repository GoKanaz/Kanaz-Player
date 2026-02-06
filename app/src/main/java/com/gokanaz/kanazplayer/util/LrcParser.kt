package com.gokanaz.kanazplayer.util

import com.gokanaz.kanazplayer.data.model.LyricLine
import java.io.File

object LrcParser {
    
    fun parseLrcFile(lrcPath: String?): List<LyricLine> {
        if (lrcPath == null) return emptyList()
        
        val file = File(lrcPath)
        if (!file.exists()) return emptyList()
        
        val lyrics = mutableListOf<LyricLine>()
        
        try {
            file.readLines().forEach { line ->
                val matches = Regex("\\[(\\d{2}):(\\d{2})\\.(\\d{2,3})\\](.*)").find(line)
                if (matches != null) {
                    val (minutes, seconds, millis, text) = matches.destructured
                    val timeInMillis = minutes.toLong() * 60000 + 
                                      seconds.toLong() * 1000 + 
                                      millis.padEnd(3, '0').toLong()
                    
                    if (text.trim().isNotEmpty()) {
                        lyrics.add(LyricLine(timeInMillis, text.trim()))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return lyrics.sortedBy { it.timeInMillis }
    }
    
    fun findLrcFile(songPath: String): String? {
        val songFile = File(songPath)
        val lrcFile = File(songFile.parent, songFile.nameWithoutExtension + ".lrc")
        return if (lrcFile.exists()) lrcFile.absolutePath else null
    }
    
    fun getCurrentLyricIndex(lyrics: List<LyricLine>, currentPosition: Long): Int {
        if (lyrics.isEmpty()) return -1
        
        for (i in lyrics.indices.reversed()) {
            if (currentPosition >= lyrics[i].timeInMillis) {
                return i
            }
        }
        return -1
    }
}
