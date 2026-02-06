package com.gokanaz.kanazplayer.ui.sleep

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SleepTimerDialog(
    isActive: Boolean,
    remainingTime: Long,
    onDismiss: () -> Unit,
    onSetTimer: (Int) -> Unit,
    onCancel: () -> Unit
) {
    val timerOptions = listOf(
        5 to "5 minutes",
        10 to "10 minutes",
        15 to "15 minutes",
        30 to "30 minutes",
        45 to "45 minutes",
        60 to "1 hour"
    )
    
    var selectedMinutes by remember { mutableStateOf(15) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (isActive) "Sleep Timer Active" else "Set Sleep Timer"
            )
        },
        text = {
            Column {
                if (isActive) {
                    Text(
                        "Music will stop in ${formatTime(remainingTime)}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    timerOptions.forEach { (minutes, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = selectedMinutes == minutes,
                                    onClick = { selectedMinutes = minutes }
                                )
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedMinutes == minutes,
                                onClick = { selectedMinutes = minutes }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(label)
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (isActive) {
                TextButton(onClick = onCancel) {
                    Text("Cancel Timer")
                }
            } else {
                TextButton(onClick = { onSetTimer(selectedMinutes) }) {
                    Text("Start")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}
