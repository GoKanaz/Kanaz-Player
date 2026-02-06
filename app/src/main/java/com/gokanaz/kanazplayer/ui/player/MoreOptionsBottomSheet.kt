package com.gokanaz.kanazplayer.ui.player

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
    var showEditTagDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val writeSettingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        song?.let { setAsRingtone(context, it) }
    }
    
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            ListItem(
                headlineContent = { Text("Edit Tag") },
                leadingContent = { Icon(Icons.Default.Edit, null) },
                modifier = Modifier.fillMaxWidth(),
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                tonalElevation = 0.dp
            )
            ListItem(
                headlineContent = { Text("Jadikan Nada Dering") },
                leadingContent = { Icon(Icons.Default.Notifications, null) },
                modifier = Modifier.fillMaxWidth(),
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                tonalElevation = 0.dp
            )
            ListItem(
                headlineContent = { Text("Tambah ke Playlist") },
                leadingContent = { Icon(Icons.Default.PlaylistAdd, null) },
                modifier = Modifier.fillMaxWidth(),
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                tonalElevation = 0.dp
            )
            ListItem(
                headlineContent = { Text("Bagikan") },
                leadingContent = { Icon(Icons.Default.Share, null) },
                modifier = Modifier.fillMaxWidth(),
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                tonalElevation = 0.dp
            )
            ListItem(
                headlineContent = { Text("Info Lagu") },
                leadingContent = { Icon(Icons.Default.Info, null) },
                modifier = Modifier.fillMaxWidth(),
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                tonalElevation = 0.dp
            )
            ListItem(
                headlineContent = { Text("Hapus") },
                leadingContent = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) },
                modifier = Modifier.fillMaxWidth(),
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                tonalElevation = 0.dp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    
    if (showEditTagDialog) {
        EditTagDialog(
            song = song,
            onDismiss = { showEditTagDialog = false }
        )
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Lagu?") },
            text = { Text("Apakah Anda yakin ingin menghapus ${song?.title}?") },
            confirmButton = {
                TextButton(onClick = {
                    song?.let { deleteSong(context, it) }
                    showDeleteDialog = false
                    onDeleteConfirm()
                }) {
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
fun EditTagDialog(
    song: Song?,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(song?.title ?: "") }
    var artist by remember { mutableStateOf(song?.artist ?: "") }
    var album by remember { mutableStateOf(song?.album ?: "") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Tag") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = artist,
                    onValueChange = { artist = it },
                    label = { Text("Artis") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = album,
                    onValueChange = { album = it },
                    label = { Text("Album") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onDismiss()
            }) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

private fun setAsRingtone(context: Context, song: Song) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (!Settings.System.canWrite(context)) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:${context.packageName}")
            Toast.makeText(context, "Izinkan modifikasi pengaturan sistem", Toast.LENGTH_LONG).show()
            return
        }
    }
    
    try {
        val file = File(song.path)
        if (!file.exists()) {
            Toast.makeText(context, "File tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }
        
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DATA, file.absolutePath)
            put(MediaStore.MediaColumns.TITLE, song.title)
            put(MediaStore.MediaColumns.MIME_TYPE, "audio/*")
            put(MediaStore.Audio.Media.IS_RINGTONE, true)
            put(MediaStore.Audio.Media.IS_NOTIFICATION, false)
            put(MediaStore.Audio.Media.IS_ALARM, false)
            put(MediaStore.Audio.Media.IS_MUSIC, false)
        }
        
        val uri = MediaStore.Audio.Media.getContentUriForPath(file.absolutePath)
        context.contentResolver.delete(
            uri!!,
            "${MediaStore.MediaColumns.DATA}=?",
            arrayOf(file.absolutePath)
        )
        
        val newUri = context.contentResolver.insert(uri, values)
        
        if (newUri != null) {
            RingtoneManager.setActualDefaultRingtoneUri(
                context,
                RingtoneManager.TYPE_RINGTONE,
                newUri
            )
            Toast.makeText(context, "Berhasil dijadikan nada dering", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Gagal menyimpan ke MediaStore", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        e.printStackTrace()
    }
}

private fun deleteSong(context: Context, song: Song) {
    try {
        val file = File(song.path)
        if (file.exists()) {
            file.delete()
        }
        
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        context.contentResolver.delete(
            uri,
            "${MediaStore.MediaColumns.DATA}=?",
            arrayOf(song.path)
        )
        
        Toast.makeText(context, "Lagu dihapus", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Gagal menghapus: ${e.message}", Toast.LENGTH_SHORT).show()
        e.printStackTrace()
    }
}
