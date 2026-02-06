package com.gokanaz.kanazplayer.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
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
        // Return null, will use icon instead
        return null
    }
}
