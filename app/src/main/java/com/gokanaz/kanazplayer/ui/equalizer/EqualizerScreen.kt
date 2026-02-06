package com.gokanaz.kanazplayer.ui.equalizer

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EqualizerScreen(
    onBackClick: () -> Unit,
    viewModel: com.gokanaz.kanazplayer.ui.player.PlayerViewModel
) {
    val bass by viewModel.bassBoost.collectAsState()
    val virtualizer by viewModel.virtualizerStrength.collectAsState()
    val equalizerEnabled by viewModel.equalizerEnabled.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Equalizer") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Enable Equalizer",
                    style = MaterialTheme.typography.titleMedium
                )
                Switch(
                    checked = equalizerEnabled,
                    onCheckedChange = { viewModel.setEqualizerEnabled(it) }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Bass Boost",
                style = MaterialTheme.typography.titleSmall
            )
            Slider(
                value = bass.toFloat(),
                onValueChange = { viewModel.setBassBoost(it.toInt()) },
                valueRange = 0f..1000f,
                enabled = equalizerEnabled
            )
            Text(
                text = "${bass}/1000",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Virtualizer",
                style = MaterialTheme.typography.titleSmall
            )
            Slider(
                value = virtualizer.toFloat(),
                onValueChange = { viewModel.setVirtualizerStrength(it.toInt()) },
                valueRange = 0f..1000f,
                enabled = equalizerEnabled
            )
            Text(
                text = "${virtualizer}/1000",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    viewModel.setBassBoost(0)
                    viewModel.setVirtualizerStrength(0)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = equalizerEnabled
            ) {
                Text("Reset to Default")
            }
        }
    }
}
