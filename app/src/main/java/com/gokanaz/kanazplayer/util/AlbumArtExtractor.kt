package com.gokanaz.kanazplayer.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AlbumArtExtractor {
    
    suspend fun extractAlbumArt(context: Context, path: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, Uri.parse(path))
            
            val art = retriever.embeddedPicture
            retriever.release()
            
            art?.let { bytes ->
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun getDefaultAlbumArt(): Bitmap? {
        return null
    }
    
    suspend fun getDominantColor(bitmap: Bitmap?): Int = withContext(Dispatchers.IO) {
        if (bitmap == null) return@withContext 0xFF1C1C1E.toInt()
        
        try {
            val palette = Palette.from(bitmap).generate()
            palette.getDarkVibrantColor(
                palette.getDarkMutedColor(0xFF1C1C1E.toInt())
            )
        } catch (e: Exception) {
            0xFF1C1C1E.toInt()
        }
    }
}
