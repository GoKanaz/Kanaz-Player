package com.gokanaz.kanazplayer.ui.player

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gokanaz.kanazplayer.data.model.LyricLine

@Composable
fun LyricsScreen(
    lyrics: List<LyricLine>,
    currentPosition: Long,
    onSeekTo: (Long) -> Unit
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    var showManualInputDialog by remember { mutableStateOf(false) }
    var manualLyrics by remember { mutableStateOf("") }
    
    val filePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                try {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        val text = inputStream.bufferedReader().use { it.readText() }
                        manualLyrics = text
                        Toast.makeText(context, "Lirik berhasil diimpor", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Gagal membaca file: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    LaunchedEffect(currentPosition, lyrics) {
        if (lyrics.isNotEmpty()) {
            val activeIndex = lyrics.indexOfLast { it.timeInMillis <= currentPosition }
            if (activeIndex >= 0) {
                listState.animateScrollToItem(activeIndex.coerceAtLeast(0))
            }
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        if (lyrics.isEmpty() && manualLyrics.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color.White.copy(alpha = 0.6f)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    "Tidak ada lirik yang ditemukan",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    "Silakan cari lirik secara online, atau tambahkan\nsecara manual.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = {
                        Toast.makeText(context, "Fitur pencarian online akan segera hadir", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B4789)
                    )
                ) {
                    Text("CARI ONLINE", fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                            type = "text/*"
                            addCategory(Intent.CATEGORY_OPENABLE)
                        }
                        filePickerLauncher.launch(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B4789)
                    )
                ) {
                    Text("IMPOR BERKAS LIRIK", fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = { showManualInputDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B4789)
                    )
                ) {
                    Text("MASUKKAN LIRIK", fontWeight = FontWeight.Bold)
                }
            }
        } else if (manualLyrics.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                item {
                    Text(
                        text = manualLyrics,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5f
                    )
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                itemsIndexed(lyrics) { index, line ->
                    val isActive = currentPosition >= line.timeInMillis &&
                            (index == lyrics.lastIndex || currentPosition < lyrics[index + 1].timeInMillis)
                    
                    Text(
                        text = line.text,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                        color = if (isActive) Color.White else Color.White.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
    
    if (showManualInputDialog) {
        var inputText by remember { mutableStateOf(manualLyrics) }
        
        AlertDialog(
            onDismissRequest = { showManualInputDialog = false },
            title = { Text("Masukkan Lirik") },
            text = {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    placeholder = { Text("Ketik atau tempel lirik di sini...") },
                    maxLines = Int.MAX_VALUE
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    manualLyrics = inputText
                    showManualInputDialog = false
                    Toast.makeText(context, "Lirik berhasil disimpan", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualInputDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
