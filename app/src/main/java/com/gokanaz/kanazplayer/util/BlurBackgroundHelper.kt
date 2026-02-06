package com.gokanaz.kanazplayer.util

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils

object BlurBackgroundHelper {
    
    fun getGradientColors(dominantColor: Int): Pair<Color, Color> {
        val topColor = Color(ColorUtils.blendARGB(dominantColor, 0xFF000000.toInt(), 0.3f))
        val bottomColor = Color(ColorUtils.blendARGB(dominantColor, 0xFF000000.toInt(), 0.7f))
        return Pair(topColor, bottomColor)
    }
    
    fun createBlurredBitmap(original: Bitmap?): Bitmap? {
        if (original == null) return null
        
        return try {
            val scale = 0.1f
            val width = (original.width * scale).toInt()
            val height = (original.height * scale).toInt()
            Bitmap.createScaledBitmap(original, width, height, true)
        } catch (e: Exception) {
            null
        }
    }
}
