package com.google.antigravity.companion.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.antigravity.companion.CompanionNavKey
import com.google.antigravity.companion.data.DefaultDataRepository
import com.google.antigravity.companion.data.SessionStatus
import com.google.antigravity.companion.ui.components.MorphableNavigationBar
import com.google.antigravity.companion.ui.portal.WebPortalScreen
import com.google.antigravity.companion.ui.remote.DesktopRemoteScreen
import com.google.antigravity.companion.ui.settings.SettingsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel = viewModel { MainScreenViewModel(DefaultDataRepository()) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    uiState.userNotice?.let { notice ->
        LaunchedEffect(notice) {
            snackbarHostState.showSnackbar(notice)
            viewModel.clearNotice()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)
    ) { _ ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Main content depending on active tab
            when (uiState.currentTab) {
                CompanionNavKey.WebPortal -> {
                    WebPortalScreen(
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
                        onTriggerCommand = viewModel::triggerSlashCommand,
                        onAddNewSession = viewModel::addNewSession
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

            // Floating Pill Navigation Bar (Material 3 Expressive, floating above content)
            val hasPendingApproval = uiState.sessions.any { it.status == SessionStatus.WAITING_APPROVAL }
            MorphableNavigationBar(
                selectedTab = uiState.currentTab,
                onTabSelected = viewModel::selectTab,
                onActionClicked = { viewModel.toggleQuickCommandSheet(true) },
                hasPendingApproval = hasPendingApproval,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
            )
        }

        // Quick Slash Commands Bottom Sheet (Google Photos / Drive style)
        if (uiState.isQuickCommandSheetVisible) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.toggleQuickCommandSheet(false) },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Instant Commands",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Trigger specialized Antigravity workflows on your desktop",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(uiState.quickCommands) { cmd ->
                            ListItem(
                                headlineContent = {
                                    Text(
                                        text = cmd.command,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                },
                                supportingContent = { Text(cmd.description) },
                                trailingContent = {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    ) {
                                        Text(
                                            text = cmd.category,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                },
                                modifier = Modifier.clickable {
                                    uiState.activeSession?.let { session ->
                                        viewModel.triggerSlashCommand(session.id, cmd.command)
                                    }
                                    viewModel.toggleQuickCommandSheet(false)
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
