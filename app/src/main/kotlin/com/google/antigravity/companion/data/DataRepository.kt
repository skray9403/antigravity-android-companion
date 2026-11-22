package com.google.antigravity.companion.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface DataRepository {
    val sessions: Flow<List<DesktopSession>>
    val activeSession: Flow<DesktopSession?>
    val logs: Flow<List<TerminalLogEntry>>
    val quickCommands: List<QuickSlashCommand>

    suspend fun selectSession(sessionId: String)
    suspend fun approvePendingTool(sessionId: String, requestId: String)
    suspend fun denyPendingTool(sessionId: String, requestId: String)
    suspend fun sendPromptToDesktop(sessionId: String, prompt: String)
    suspend fun triggerSlashCommand(sessionId: String, command: String)
    suspend fun updateHostConfig(sessionId: String, newHost: String, newName: String)
    suspend fun addSession(name: String, host: String)
    suspend fun removeSession(sessionId: String)
}

class DefaultDataRepository : DataRepository {

    private val _sessions = MutableStateFlow<List<DesktopSession>>(emptyList())
    override val sessions: Flow<List<DesktopSession>> = _sessions.asStateFlow()

    private val _activeSessionId = MutableStateFlow("")
    private val _activeSession = MutableStateFlow<DesktopSession?>(null)
    override val activeSession: Flow<DesktopSession?> = _activeSession.asStateFlow()

    private val _logs = MutableStateFlow<List<TerminalLogEntry>>(emptyList())
    override val logs: Flow<List<TerminalLogEntry>> = _logs.asStateFlow()

    override val quickCommands: List<QuickSlashCommand> = listOf(
        QuickSlashCommand("/goal", "Goal Run", "Run autonomous long-running task until completion", "Workflow", "Flag"),
        QuickSlashCommand("/plan", "Plan Task", "Create structured step-by-step implementation plan", "Planning", "ListAlt"),
        QuickSlashCommand("/compact", "Compact Context", "Summarize context to free up active window", "Context", "Compress"),
        QuickSlashCommand("/status", "Status Check", "Report live agent states & active task queue", "Agent", "Info"),
        QuickSlashCommand("/browser", "Browser Automation", "Activate headless browser tool capabilities", "Tool", "Language"),
        QuickSlashCommand("/boost", "Deep Boost", "Activate deep reasoning with multi-agent verification", "Workflow", "Bolt"),
        QuickSlashCommand("/teamwork", "Agent Swarm", "Deploy cooperative swarm of autonomous agents", "Swarm", "Groups"),
        QuickSlashCommand("/kill", "Emergency Stop", "Immediately terminate running agent processes", "Safety", "StopCircle")
    )

    override suspend fun selectSession(sessionId: String) {
        _activeSessionId.value = sessionId
        _activeSession.value = _sessions.value.find { it.id == sessionId }
    }

    override suspend fun approvePendingTool(sessionId: String, requestId: String) {
        _sessions.update { list ->
            list.map { session ->
                if (session.id == sessionId && session.pendingApproval?.id == requestId) {
                    session.copy(
                        status = SessionStatus.RUNNING,
                        pendingApproval = null,
                        activeTask = "Approved: Executing tool [${session.pendingApproval.toolName}]"
                    )
                } else {
                    session
                }
            }
        }
        _activeSession.value = _sessions.value.find { it.id == _activeSessionId.value }
        _logs.update {
            it + TerminalLogEntry(
                timestamp = "Now",
                level = "INFO",
                message = "Approved request $requestId from companion app. Execution started."
            )
        }
    }

    override suspend fun denyPendingTool(sessionId: String, requestId: String) {
        _sessions.update { list ->
            list.map { session ->
                if (session.id == sessionId && session.pendingApproval?.id == requestId) {
                    session.copy(
                        status = SessionStatus.IDLE,
                        pendingApproval = null,
                        activeTask = "Denied tool execution. Standing by for instructions."
                    )
                } else {
                    session
                }
            }
        }
        _activeSession.value = _sessions.value.find { it.id == _activeSessionId.value }
        _logs.update {
            it + TerminalLogEntry(
                timestamp = "Now",
                level = "WARN",
                message = "Denied request $requestId from companion app. Agent paused."
            )
        }
    }

    override suspend fun sendPromptToDesktop(sessionId: String, prompt: String) {
        _sessions.update { list ->
            list.map { session ->
                if (session.id == sessionId) {
                    session.copy(
                        status = SessionStatus.RUNNING,
                        activeTask = "Processing prompt: \"$prompt\""
                    )
                } else {
                    session
                }
            }
        }
        _activeSession.value = _sessions.value.find { it.id == _activeSessionId.value }
        _logs.update {
            it + TerminalLogEntry(
                timestamp = "Now",
                level = "AGENT",
                message = "Companion prompt sent -> \"$prompt\""
            )
        }
    }

    override suspend fun triggerSlashCommand(sessionId: String, command: String) {
        _sessions.update { list ->
            list.map { session ->
                if (session.id == sessionId) {
                    session.copy(
                        status = SessionStatus.RUNNING,
                        activeTask = "Executing slash command $command"
                    )
                } else {
                    session
                }
            }
        }
        _activeSession.value = _sessions.value.find { it.id == _activeSessionId.value }
        _logs.update {
            it + TerminalLogEntry(
                timestamp = "Now",
                level = "INFO",
                message = "Slash command triggered from companion: $command"
            )
        }
    }

    override suspend fun updateHostConfig(sessionId: String, newHost: String, newName: String) {
        _sessions.update { list ->
            list.map {
                if (it.id == sessionId) {
                    it.copy(hostUrl = newHost, name = newName)
                } else {
                    it
                }
            }
        }
        _activeSession.value = _sessions.value.find { it.id == _activeSessionId.value }
    }

    override suspend fun addSession(name: String, host: String) {
        val newSession = DesktopSession(
            id = "desktop-${System.currentTimeMillis()}",
            name = name.ifBlank { "My Workstation" },
            hostUrl = host.ifBlank { "http://127.0.0.1:8080" },
            status = SessionStatus.IDLE,
            activeTask = "Standing by for instructions",
            currentWorkspace = "Default Workspace",
            tokenUsage = TokenUsage(0, 0, 0.0),
            isPrimary = _sessions.value.isEmpty()
        )
        _sessions.update { it + newSession }
        if (_activeSessionId.value.isEmpty() || _activeSession.value == null) {
            _activeSessionId.value = newSession.id
            _activeSession.value = newSession
        }
        _logs.update {
            it + TerminalLogEntry(
                timestamp = "Now",
                level = "INFO",
                message = "Connected to ${newSession.name} (${newSession.hostUrl})"
            )
        }
    }

    override suspend fun removeSession(sessionId: String) {
        _sessions.update { list -> list.filter { it.id != sessionId } }
        if (_activeSessionId.value == sessionId) {
            val next = _sessions.value.firstOrNull()
            _activeSessionId.value = next?.id ?: ""
            _activeSession.value = next
        }
    }
}
