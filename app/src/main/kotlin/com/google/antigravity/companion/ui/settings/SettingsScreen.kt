package com.google.antigravity.companion.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.antigravity.companion.data.DesktopSession

/**
 * Clean, restrained Google Settings-style implementation.
 * Follows first-party Android guidelines: page hierarchy, clear typography,
 * consistent ListItem rows, subtle dividers, and zero decorative card clutter.
 */
@Composable
fun SettingsScreen(
    activeSession: DesktopSession?,
    onSaveHostConfig: (sessionId: String, host: String, name: String) -> Unit,
    onAddNewSession: (name: String, host: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var hostUrlInput by remember(activeSession) { mutableStateOf(activeSession?.hostUrl ?: "http://192.168.1.105:8080") }
    var sessionNameInput by remember(activeSession) { mutableStateOf(activeSession?.name ?: "Studio Workstation") }

    var enableMobileCss by remember { mutableStateOf(true) }
    var enableHaptics by remember { mutableStateOf(true) }
    var keepScreenAwake by remember { mutableStateOf(true) }
    var autoDiscoverLan by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 24.dp, bottom = 120.dp)
    ) {
        // Page Header (Google Settings Style)
        item {
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Desktop sync and application preferences",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Section: Desktop Pairing
        item {
            SectionHeader(title = "Desktop Pairing")
        }

        item {
            ListItem(
                headlineContent = { Text(activeSession?.name ?: "Configure Host", fontWeight = FontWeight.Medium) },
                supportingContent = { Text(activeSession?.hostUrl ?: "Tap to set pairing URL") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Computer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                modifier = Modifier.clickable { showEditDialog = true }
            )
        }

        item {
            ListItem(
                headlineContent = { Text("Local Network Discovery") },
                supportingContent = { Text("Scan local subnet for active Antigravity CLI daemons") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Sensors,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                },
                trailingContent = {
                    Switch(
                        checked = autoDiscoverLan,
                        onCheckedChange = { autoDiscoverLan = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                    )
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )
        }

        item {
            HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)
        }

        // Section: Web Experience
        item {
            SectionHeader(title = "Web Experience")
        }

        item {
            ListItem(
                headlineContent = { Text("Mobile Viewport Optimization") },
                supportingContent = { Text("Auto-scale antigravity.google.com touch targets & code overflow") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                },
                trailingContent = {
                    Switch(
                        checked = enableMobileCss,
                        onCheckedChange = { enableMobileCss = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                    )
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )
        }

        item {
            ListItem(
                headlineContent = { Text("Keep Awake During Agent Runs") },
                supportingContent = { Text("Prevent device sleep while desktop agent is executing tasks") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                },
                trailingContent = {
                    Switch(
                        checked = keepScreenAwake,
                        onCheckedChange = { keepScreenAwake = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                    )
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )
        }

        item {
            HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)
        }

        // Section: Interaction & Feedback
        item {
            SectionHeader(title = "Interaction & Feedback")
        }

        item {
            ListItem(
                headlineContent = { Text("Tactile Haptics") },
                supportingContent = { Text("Physical vibration when approving agent tools or commands") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Vibration,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                },
                trailingContent = {
                    Switch(
                        checked = enableHaptics,
                        onCheckedChange = { enableHaptics = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                    )
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )
        }

        item {
            HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)
        }

        // Section: About
        item {
            SectionHeader(title = "About")
        }

        item {
            ListItem(
                headlineContent = { Text("Antigravity Companion") },
                supportingContent = { Text("Version 1.0.0 • Google Material 3 Expressive") },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
            )
        }
    }

    // Google-style clean Dialog for editing pairing address
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Configure Desktop Host") },
            text = {
                Column {
                    OutlinedTextField(
                        value = sessionNameInput,
                        onValueChange = { sessionNameInput = it },
                        label = { Text("Session Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = hostUrlInput,
                        onValueChange = { hostUrlInput = it },
                        label = { Text("Host Endpoint (IP / URL)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        activeSession?.let { onSaveHostConfig(it.id, hostUrlInput, sessionNameInput) }
                        showEditDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
    )
}
