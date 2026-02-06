package com.gokanaz.kanazplayer.ui.player

import android.content.Context
import android.media.AudioManager
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun VolumeControlDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    
    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    var currentVolume by remember { 
        mutableStateOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()) 
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Volume") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = if (currentVolume == 0f) Icons.Default.VolumeOff 
                                     else Icons.Default.VolumeUp,
                        contentDescription = null
                    )
                    
                    Slider(
                        value = currentVolume,
                        onValueChange = { newValue ->
                            currentVolume = newValue
                            audioManager.setStreamVolume(
                                AudioManager.STREAM_MUSIC,
                                newValue.toInt(),
                                0
                            )
                        },
                        valueRange = 0f..maxVolume.toFloat(),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                    )
                    
                    Text("${(currentVolume / maxVolume * 100).toInt()}%")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}
