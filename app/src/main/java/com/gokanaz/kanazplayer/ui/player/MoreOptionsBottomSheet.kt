package com.gokanaz.kanazplayer.ui.player

import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.gokanaz.kanazplayer.data.model.Song
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreOptionsBottomSheet(
    song: Song?,
    onDismiss: () -> Unit,
    onDeleteConfirm: () -> Unit
) {
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = song?.title ?: "No Song",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )
            
            Divider()
            
            OptionItem(
                icon = Icons.Default.Edit,
                title = "Edit Tag",
                onClick = {
                    onDismiss()
                }
            )
            
            OptionItem(
                icon = Icons.Default.MusicNote,
                title = "Jadikan Nada Dering",
                onClick = {
                    song?.let { setAsRingtone(context, it) }
                    onDismiss()
                }
            )
            
            OptionItem(
                icon = Icons.Default.Share,
                title = "Bagikan Lagu",
                onClick = {
                    song?.let { shareSong(context, it) }
                    onDismiss()
                }
            )
            
            OptionItem(
                icon = Icons.Default.Delete,
                title = "Hapus File",
                isDestructive = true,
                onClick = {
                    showDeleteDialog = true
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus File?") },
            text = { Text("File ini akan dihapus secara permanen dari perangkat Anda.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        song?.let { deleteSongFile(context, it) }
                        showDeleteDialog = false
                        onDeleteConfirm()
                        onDismiss()
                    }
                ) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun OptionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isDestructive) MaterialTheme.colorScheme.error 
                   else MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isDestructive) MaterialTheme.colorScheme.error 
                    else MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun setAsRingtone(context: Context, song: Song) {
    try {
        val contentValues = android.content.ContentValues().apply {
            put(MediaStore.MediaColumns.IS_RINGTONE, true)
        }
        context.contentResolver.update(
            Uri.parse(song.path),
            contentValues,
            null,
            null
        )
        RingtoneManager.setActualDefaultRingtoneUri(
            context,
            RingtoneManager.TYPE_RINGTONE,
            Uri.parse(song.path)
        )
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun shareSong(context: Context, song: Song) {
    try {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "audio/*"
            putExtra(Intent.EXTRA_STREAM, Uri.parse(song.path))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Bagikan lagu"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun deleteSongFile(context: Context, song: Song) {
    try {
        val file = File(song.path)
        file.delete()
        
        context.contentResolver.delete(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            "${MediaStore.Audio.Media._ID} = ?",
            arrayOf(song.id.toString())
        )
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
