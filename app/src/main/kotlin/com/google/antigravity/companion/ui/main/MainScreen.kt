package com.google.antigravity.companion.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.antigravity.companion.CompanionNavKey
import com.google.antigravity.companion.data.DefaultDataRepository
import com.google.antigravity.companion.data.SessionStatus
import com.google.antigravity.companion.ui.portal.WebPortalScreen
import com.google.antigravity.companion.ui.remote.DesktopRemoteScreen
import com.google.antigravity.companion.ui.settings.SettingsScreen
import com.google.antigravity.companion.ui.theme.AntigravityAmber
import com.google.antigravity.companion.ui.theme.AntigravityBorder
import com.google.antigravity.companion.ui.theme.AntigravityCyan
import com.google.antigravity.companion.ui.theme.AntigravityPurple
import com.google.antigravity.companion.ui.theme.AntigravitySurface
import com.google.antigravity.companion.ui.theme.AntigravitySurfaceElevated
import com.google.antigravity.companion.ui.theme.AntigravityTextMuted
import com.google.antigravity.companion.ui.theme.AntigravityTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel = viewModel { MainScreenViewModel(DefaultDataRepository()) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Show snackbar notice if triggered
    uiState.userNotice?.let { notice ->
        LaunchedEffect(notice) {
            snackbarHostState.showSnackbar(notice)
            viewModel.clearNotice()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = AntigravitySurface,
                tonalElevation = 8.dp
            ) {
                // Tab 1: Web Portal
                NavigationBarItem(
                    selected = uiState.currentTab == CompanionNavKey.WebPortal,
                    onClick = { viewModel.selectTab(CompanionNavKey.WebPortal) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Web Portal"
                        )
                    },
                    label = { Text("Web Portal") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = AntigravityPurple,
                        indicatorColor = AntigravityPurple,
                        unselectedIconColor = AntigravityTextMuted,
                        unselectedTextColor = AntigravityTextMuted
                    )
                )

                // Tab 2: Desktop Remote Controller
                val hasPendingApproval = uiState.sessions.any { it.status == SessionStatus.WAITING_APPROVAL }
                NavigationBarItem(
                    selected = uiState.currentTab == CompanionNavKey.DesktopRemote,
                    onClick = { viewModel.selectTab(CompanionNavKey.DesktopRemote) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (hasPendingApproval) {
                                    Badge(
                                        containerColor = AntigravityAmber,
                                        modifier = Modifier.size(8.dp)
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DesktopWindows,
                                contentDescription = "Desktop Remote"
                            )
                        }
                    },
                    label = { Text("Remote Hub") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = AntigravityPurple,
                        indicatorColor = AntigravityPurple,
                        unselectedIconColor = AntigravityTextMuted,
                        unselectedTextColor = AntigravityTextMuted
                    )
                )

                // Tab 3: Settings
                NavigationBarItem(
                    selected = uiState.currentTab == CompanionNavKey.Settings,
                    onClick = { viewModel.selectTab(CompanionNavKey.Settings) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Settings"
                        )
                    },
                    label = { Text("Settings") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = AntigravityPurple,
                        indicatorColor = AntigravityPurple,
                        unselectedIconColor = AntigravityTextMuted,
                        unselectedTextColor = AntigravityTextMuted
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.currentTab) {
                CompanionNavKey.WebPortal -> {
                    WebPortalScreen(
                        onOpenSlashCommands = { viewModel.toggleQuickCommandSheet(true) },
                        onNavigateToRemote = { viewModel.selectTab(CompanionNavKey.DesktopRemote) }
                    )
                }

                CompanionNavKey.DesktopRemote -> {
                    DesktopRemoteScreen(
                        sessions = uiState.sessions,
                        activeSession = uiState.activeSession,
                        logs = uiState.logs,
                        quickCommands = uiState.quickCommands,
                        onSelectSession = viewModel::selectSession,
                        onApproveTool = viewModel::approveTool,
                        onDenyTool = viewModel::denyTool,
                        onSendPrompt = viewModel::sendPrompt,
                        onTriggerCommand = viewModel::triggerSlashCommand
                    )
                }

                CompanionNavKey.Settings -> {
                    SettingsScreen(
                        activeSession = uiState.activeSession,
                        onSaveHostConfig = viewModel::updateHostConfig,
                        onAddNewSession = viewModel::addNewSession
                    )
                }
            }
        }

        // Quick Slash Commands Bottom Sheet
        if (uiState.isQuickCommandSheetVisible) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.toggleQuickCommandSheet(false) },
                sheetState = sheetState,
                containerColor = AntigravitySurface,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(AntigravityPurple),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = "Instant Slash Commands",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = "Trigger specialized agent behaviors and workflows on your active desktop session directly from your phone:",
                        style = MaterialTheme.typography.bodySmall,
                        color = AntigravityTextSecondary
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.quickCommands) { cmd ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = AntigravitySurfaceElevated,
                                border = androidx.compose.foundation.BorderStroke(1.dp, AntigravityBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        uiState.activeSession?.let { session ->
                                            viewModel.triggerSlashCommand(session.id, cmd.command)
                                        }
                                        viewModel.toggleQuickCommandSheet(false)
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = cmd.command,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace,
                                                color = AntigravityCyan
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = AntigravityPurple.copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    text = cmd.category,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = AntigravityPurple,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = cmd.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = AntigravityTextMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
