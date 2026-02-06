package com.gokanaz.kanazplayer.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                ListItem(
                    headlineContent = { Text("Dark Mode") },
                    supportingContent = { Text("Switch between light and dark theme") },
                    leadingContent = {
                        Icon(
                            Icons.Default.DarkMode,
                            contentDescription = null
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = onThemeChange
                        )
                    }
                )
            }
            
            item { HorizontalDivider() }
            
            item {
                ListItem(
                    headlineContent = { Text("About") },
                    supportingContent = { Text("Kanaz Player v1.0") },
                    leadingContent = {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }
}
