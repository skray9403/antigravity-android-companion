package com.google.antigravity.companion.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.antigravity.companion.data.DesktopSession
import com.google.antigravity.companion.ui.theme.AntigravityBorder
import com.google.antigravity.companion.ui.theme.AntigravityCyan
import com.google.antigravity.companion.ui.theme.AntigravityPurple
import com.google.antigravity.companion.ui.theme.AntigravitySurfaceCard
import com.google.antigravity.companion.ui.theme.AntigravitySurfaceElevated
import com.google.antigravity.companion.ui.theme.AntigravityTextMuted
import com.google.antigravity.companion.ui.theme.AntigravityTextSecondary

@Composable
fun SettingsScreen(
    activeSession: DesktopSession?,
    onSaveHostConfig: (sessionId: String, host: String, name: String) -> Unit,
    onAddNewSession: (name: String, host: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var hostUrlInput by remember(activeSession) { mutableStateOf(activeSession?.hostUrl ?: "http://192.168.1.105:8080") }
    var sessionNameInput by remember(activeSession) { mutableStateOf(activeSession?.name ?: "Studio Workstation") }

    var enableMobileCss by remember { mutableStateOf(true) }
    var enableHaptics by remember { mutableStateOf(true) }
    var enableNotifications by remember { mutableStateOf(true) }
    var keepScreenAwake by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Companion Settings",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Configure Antigravity desktop pairing & mobile optimization engine",
                style = MaterialTheme.typography.bodySmall,
                color = AntigravityTextMuted
            )
        }

        // 1. Desktop Host Pairing Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AntigravitySurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, AntigravityBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Link, contentDescription = null, tint = AntigravityCyan)
                        Text(
                            text = "Desktop Pairing Configuration",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    OutlinedTextField(
                        value = sessionNameInput,
                        onValueChange = { sessionNameInput = it },
                        label = { Text("Desktop Alias Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AntigravityPurple,
                            unfocusedBorderColor = AntigravityBorder,
                            focusedContainerColor = AntigravitySurfaceElevated,
                            unfocusedContainerColor = AntigravitySurfaceElevated
                        )
                    )

                    OutlinedTextField(
                        value = hostUrlInput,
                        onValueChange = { hostUrlInput = it },
                        label = { Text("Antigravity Host URL / WebSocket IP") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AntigravityPurple,
                            unfocusedBorderColor = AntigravityBorder,
                            focusedContainerColor = AntigravitySurfaceElevated,
                            unfocusedContainerColor = AntigravitySurfaceElevated
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                activeSession?.let {
                                    onSaveHostConfig(it.id, hostUrlInput, sessionNameInput)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Update Active Host")
                        }
                        Button(
                            onClick = {
                                if (sessionNameInput.isNotBlank() && hostUrlInput.isNotBlank()) {
                                    onAddNewSession(sessionNameInput, hostUrlInput)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("Add Device")
                        }
                    }
                }
            }
        }

        // 2. Mobile Engine Preferences
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AntigravitySurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, AntigravityBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Smartphone, contentDescription = null, tint = AntigravityPurple)
                        Text(
                            text = "Mobile Optimizations",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Toggles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Mobile Layout Injection", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                            Text("Auto-adjust viewport and code overflow on antigravity.google.com", style = MaterialTheme.typography.bodySmall, color = AntigravityTextMuted)
                        }
                        Switch(
                            checked = enableMobileCss,
                            onCheckedChange = { enableMobileCss = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = AntigravityPurple)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Tactile Haptic Feedback", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                            Text("Vibrate on tool approval and slash commands", style = MaterialTheme.typography.bodySmall, color = AntigravityTextMuted)
                        }
                        Switch(
                            checked = enableHaptics,
                            onCheckedChange = { enableHaptics = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = AntigravityPurple)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Keep Awake During Task", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                            Text("Prevent device sleep while agent is actively running", style = MaterialTheme.typography.bodySmall, color = AntigravityTextMuted)
                        }
                        Switch(
                            checked = keepScreenAwake,
                            onCheckedChange = { keepScreenAwake = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = AntigravityPurple)
                        )
                    }
                }
            }
        }

        // 3. Information & Documentation
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AntigravitySurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, AntigravityBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = AntigravityCyan)
                        Text(
                            text = "About Antigravity Companion",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "Antigravity Mobile Companion v1.0.0\nDesigned for full-fidelity touch orchestration of desktop Antigravity sessions and real-time monitoring of agent workflows.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AntigravityTextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
