package com.google.antigravity.companion.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.antigravity.companion.CompanionNavKey
import com.google.antigravity.companion.data.DataRepository
import com.google.antigravity.companion.data.DesktopSession
import com.google.antigravity.companion.data.QuickSlashCommand
import com.google.antigravity.companion.data.TerminalLogEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MainUiState(
    val sessions: List<DesktopSession> = emptyList(),
    val activeSession: DesktopSession? = null,
    val logs: List<TerminalLogEntry> = emptyList(),
    val quickCommands: List<QuickSlashCommand> = emptyList(),
    val currentTab: CompanionNavKey = CompanionNavKey.WebPortal,
    val isQuickCommandSheetVisible: Boolean = false,
    val userNotice: String? = null
)

class MainScreenViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _currentTab = MutableStateFlow<CompanionNavKey>(CompanionNavKey.WebPortal)
    private val _isQuickCommandSheetVisible = MutableStateFlow(false)
    private val _userNotice = MutableStateFlow<String?>(null)

    private val sessionStateFlow = combine(
        dataRepository.sessions,
        dataRepository.activeSession,
        dataRepository.logs
    ) { sessions, activeSession, logs ->
        Triple(sessions, activeSession, logs)
    }

    val uiState: StateFlow<MainUiState> = combine(
        sessionStateFlow,
        _currentTab,
        _isQuickCommandSheetVisible,
        _userNotice
    ) { (sessions, activeSession, logs), tab, showSheet, notice ->
        MainUiState(
            sessions = sessions,
            activeSession = activeSession,
            logs = logs,
            quickCommands = dataRepository.quickCommands,
            currentTab = tab,
            isQuickCommandSheetVisible = showSheet,
            userNotice = notice
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        MainUiState(quickCommands = dataRepository.quickCommands)
    )

    fun selectTab(tab: CompanionNavKey) {
        _currentTab.value = tab
    }

    fun toggleQuickCommandSheet(visible: Boolean) {
        _isQuickCommandSheetVisible.value = visible
    }

    fun selectSession(sessionId: String) {
        viewModelScope.launch {
            dataRepository.selectSession(sessionId)
        }
    }

    fun approveTool(sessionId: String, requestId: String) {
        viewModelScope.launch {
            dataRepository.approvePendingTool(sessionId, requestId)
            _userNotice.value = "Approved execution on desktop."
        }
    }

    fun denyTool(sessionId: String, requestId: String) {
        viewModelScope.launch {
            dataRepository.denyPendingTool(sessionId, requestId)
            _userNotice.value = "Denied tool execution."
        }
    }

    fun sendPrompt(sessionId: String, prompt: String) {
        viewModelScope.launch {
            dataRepository.sendPromptToDesktop(sessionId, prompt)
            _userNotice.value = "Prompt sent to desktop agent."
        }
    }

    fun triggerSlashCommand(sessionId: String, command: String) {
        viewModelScope.launch {
            dataRepository.triggerSlashCommand(sessionId, command)
            _userNotice.value = "Triggered $command on desktop."
        }
    }

    fun updateHostConfig(sessionId: String, newHost: String, newName: String) {
        viewModelScope.launch {
            dataRepository.updateHostConfig(sessionId, newHost, newName)
            _userNotice.value = "Updated host configuration."
        }
    }

    fun addNewSession(name: String, host: String) {
        viewModelScope.launch {
            dataRepository.addSession(name, host)
            _userNotice.value = "Added $name to companion."
        }
    }

    fun clearNotice() {
        _userNotice.value = null
    }
}
