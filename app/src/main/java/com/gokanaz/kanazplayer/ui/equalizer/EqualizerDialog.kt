package com.gokanaz.kanazplayer.ui.equalizer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gokanaz.kanazplayer.ui.player.PlayerViewModel

@Composable
fun EqualizerDialog(
    viewModel: PlayerViewModel,
    onDismiss: () -> Unit
) {
    val bass by viewModel.bassBoost.collectAsState()
    val virtualizer by viewModel.virtualizerStrength.collectAsState()
    val equalizerEnabled by viewModel.equalizerEnabled.collectAsState()
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Equalizer") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Enable Equalizer")
                    Switch(
                        checked = equalizerEnabled,
                        onCheckedChange = { viewModel.setEqualizerEnabled(it) }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Bass Boost: $bass")
                Slider(
                    value = bass.toFloat(),
                    onValueChange = { viewModel.setBassBoost(it.toInt()) },
                    valueRange = 0f..1000f,
                    enabled = equalizerEnabled
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Virtualizer: $virtualizer")
                Slider(
                    value = virtualizer.toFloat(),
                    onValueChange = { viewModel.setVirtualizerStrength(it.toInt()) },
                    valueRange = 0f..1000f,
                    enabled = equalizerEnabled
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                viewModel.setBassBoost(0)
                viewModel.setVirtualizerStrength(0)
            }) {
                Text("Reset")
            }
        }
    )
}
