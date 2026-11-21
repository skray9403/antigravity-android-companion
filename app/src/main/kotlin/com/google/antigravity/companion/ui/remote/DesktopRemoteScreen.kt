package com.google.antigravity.companion.ui.remote

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.antigravity.companion.data.DesktopSession
import com.google.antigravity.companion.data.QuickSlashCommand
import com.google.antigravity.companion.data.RiskLevel
import com.google.antigravity.companion.data.SessionStatus
import com.google.antigravity.companion.data.TerminalLogEntry
import com.google.antigravity.companion.ui.theme.AntigravityAmber
import com.google.antigravity.companion.ui.theme.AntigravityBorder
import com.google.antigravity.companion.ui.theme.AntigravityCyan
import com.google.antigravity.companion.ui.theme.AntigravityError
import com.google.antigravity.companion.ui.theme.AntigravityGreen
import com.google.antigravity.companion.ui.theme.AntigravityPurple
import com.google.antigravity.companion.ui.theme.AntigravitySurfaceCard
import com.google.antigravity.companion.ui.theme.AntigravitySurfaceElevated
import com.google.antigravity.companion.ui.theme.AntigravityTextMuted
import com.google.antigravity.companion.ui.theme.AntigravityTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DesktopRemoteScreen(
    sessions: List<DesktopSession>,
    activeSession: DesktopSession?,
    logs: List<TerminalLogEntry>,
    quickCommands: List<QuickSlashCommand>,
    onSelectSession: (String) -> Unit,
    onApproveTool: (sessionId: String, requestId: String) -> Unit,
    onDenyTool: (sessionId: String, requestId: String) -> Unit,
    onSendPrompt: (sessionId: String, prompt: String) -> Unit,
    onTriggerCommand: (sessionId: String, command: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var promptInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Session Switcher Pills
        item {
            Text(
                text = "Paired Antigravity Desktops",
                style = MaterialTheme.typography.labelLarge,
                color = AntigravityTextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sessions) { session ->
                    val isSelected = session.id == activeSession?.id
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) AntigravityPurple else AntigravitySurfaceElevated,
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, AntigravityBorder),
                        modifier = Modifier.clickable { onSelectSession(session.id) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val dotColor = when (session.status) {
                                SessionStatus.RUNNING -> AntigravityGreen
                                SessionStatus.WAITING_APPROVAL -> AntigravityAmber
                                SessionStatus.IDLE -> AntigravityCyan
                                SessionStatus.ERROR -> AntigravityError
                                SessionStatus.DISCONNECTED -> AntigravityTextMuted
                            }
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(dotColor)
                            )
                            Text(
                                text = session.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // 2. Active Session Hero Dashboard
        activeSession?.let { session ->
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = AntigravitySurfaceCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AntigravityBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Title row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Computer,
                                    contentDescription = null,
                                    tint = AntigravityCyan,
                                    modifier = Modifier.size(24.dp)
                                )
                                Column {
                                    Text(
                                        text = session.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = session.hostUrl,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AntigravityTextMuted
                                    )
                                }
                            }

                            // Status Chip
                            val (statusText, statusBg, statusColor) = when (session.status) {
                                SessionStatus.RUNNING -> Triple("RUNNING", AntigravityGreen.copy(alpha = 0.2f), AntigravityGreen)
                                SessionStatus.WAITING_APPROVAL -> Triple("APPROVAL REQ", AntigravityAmber.copy(alpha = 0.2f), AntigravityAmber)
                                SessionStatus.IDLE -> Triple("IDLE", AntigravityCyan.copy(alpha = 0.2f), AntigravityCyan)
                                SessionStatus.ERROR -> Triple("ERROR", AntigravityError.copy(alpha = 0.2f), AntigravityError)
                                SessionStatus.DISCONNECTED -> Triple("OFFLINE", AntigravityTextMuted.copy(alpha = 0.2f), AntigravityTextMuted)
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = statusBg
                            ) {
                                Text(
                                    text = statusText,
                                    color = statusColor,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Current Active Task
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AntigravitySurfaceElevated,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "CURRENT TASK",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AntigravityTextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = session.activeTask,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Folder,
                                        contentDescription = null,
                                        tint = AntigravityTextMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = session.currentWorkspace,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AntigravityTextMuted,
                                        fontFamily = FontFamily.Monospace,
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        // Metrics Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Tokens Used", style = MaterialTheme.typography.labelSmall, color = AntigravityTextMuted)
                                Text("${session.tokenUsage.inputTokens + session.tokenUsage.outputTokens}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Subagents", style = MaterialTheme.typography.labelSmall, color = AntigravityTextMuted)
                                Text("${session.subagentsCount} active", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = AntigravityCyan)
                            }
                            Column {
                                Text("Est. Cost", style = MaterialTheme.typography.labelSmall, color = AntigravityTextMuted)
                                Text("\$${String.format("%.3f", session.tokenUsage.estimatedCostUsd)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 3. Urgent Tool Approval Banner (High priority card)
            session.pendingApproval?.let { approval ->
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AntigravitySurfaceElevated),
                        border = androidx.compose.foundation.BorderStroke(2.dp, AntigravityAmber),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Approval Required",
                                    tint = AntigravityAmber,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Desktop Tool Approval Required",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AntigravityAmber
                                )
                            }

                            Text(
                                text = "Tool: ${approval.toolName}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Monospace code snippet of command
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.Black,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = approval.commandOrDetails,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily.Monospace,
                                    color = AntigravityCyan,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }

                            // Action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { onApproveTool(session.id, approval.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = AntigravityGreen),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Approve")
                                }
                                OutlinedButton(
                                    onClick = { onDenyTool(session.id, approval.id) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AntigravityError),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, AntigravityError),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Deny")
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Quick Slash Command Matrix
        item {
            Text(
                text = "Quick Desktop Slash Commands",
                style = MaterialTheme.typography.labelLarge,
                color = AntigravityTextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 4
            ) {
                quickCommands.forEach { cmd ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AntigravitySurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, AntigravityBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                activeSession?.let { onTriggerCommand(it.id, cmd.command) }
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = cmd.command,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = AntigravityPurple,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = cmd.title,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // 5. Remote Prompt Bar
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AntigravitySurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, AntigravityBorder)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Dispatch Prompt to Desktop Session",
                        style = MaterialTheme.typography.labelMedium,
                        color = AntigravityTextSecondary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = promptInput,
                            onValueChange = { promptInput = it },
                            placeholder = { Text("E.g., /goal run all integration tests", fontSize = 13.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AntigravityPurple,
                                unfocusedBorderColor = AntigravityBorder,
                                focusedContainerColor = AntigravitySurfaceElevated,
                                unfocusedContainerColor = AntigravitySurfaceElevated
                            ),
                            singleLine = true
                        )
                        IconButton(
                            onClick = {
                                if (promptInput.isNotBlank() && activeSession != null) {
                                    onSendPrompt(activeSession.id, promptInput)
                                    promptInput = ""
                                }
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(AntigravityPurple)
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                        }
                    }
                }
            }
        }

        // 6. Live Agent Terminal Console
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF07080B)),
                border = androidx.compose.foundation.BorderStroke(1.dp, AntigravityBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Terminal,
                                contentDescription = null,
                                tint = AntigravityGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Desktop Agent Console Stream",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "LIVE",
                            style = MaterialTheme.typography.labelSmall,
                            color = AntigravityGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        logs.takeLast(6).forEach { log ->
                            val levelColor = when (log.level) {
                                "WARN" -> AntigravityAmber
                                "TOOL" -> AntigravityCyan
                                "AGENT" -> AntigravityPurple
                                else -> AntigravityTextMuted
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = log.timestamp,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = AntigravityTextMuted
                                )
                                Text(
                                    text = "[${log.level}]",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = levelColor
                                )
                                Text(
                                    text = log.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
