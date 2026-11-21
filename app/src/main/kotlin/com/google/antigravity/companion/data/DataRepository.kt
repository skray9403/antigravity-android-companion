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
}

class DefaultDataRepository : DataRepository {

    private val _sessions = MutableStateFlow<List<DesktopSession>>(
        listOf(
            DesktopSession(
                id = "desktop-main",
                name = "Studio Workstation",
                hostUrl = "http://192.168.1.105:8080",
                status = SessionStatus.WAITING_APPROVAL,
                activeTask = "Refactoring Gradle build and generating CI/CD workflows",
                currentWorkspace = "C:\\Projects\\Antigravity",
                pendingApproval = ToolApprovalRequest(
                    id = "req-9821",
                    toolName = "run_command",
                    commandOrDetails = "git push -u origin main && ./gradlew assembleRelease",
                    riskLevel = RiskLevel.CRITICAL
                ),
                tokenUsage = TokenUsage(
                    inputTokens = 42800,
                    outputTokens = 6910,
                    estimatedCostUsd = 0.04
                ),
                subagentsCount = 2,
                isPrimary = true
            ),
            DesktopSession(
                id = "desktop-laptop",
                name = "MacBook Pro Dev",
                hostUrl = "http://192.168.1.142:8080",
                status = SessionStatus.RUNNING,
                activeTask = "Background unit test suite execution on module :core",
                currentWorkspace = "/Users/developer/code/antigravity-core",
                pendingApproval = null,
                tokenUsage = TokenUsage(
                    inputTokens = 18450,
                    outputTokens = 2390,
                    estimatedCostUsd = 0.01
                ),
                subagentsCount = 1,
                isPrimary = false
            ),
            DesktopSession(
                id = "cloud-devbox",
                name = "Cloud GPU Instance (A100)",
                hostUrl = "https://cloud-agent.internal.net:8443",
                status = SessionStatus.IDLE,
                activeTask = "Standing by for distributed swarm inference task",
                currentWorkspace = "/workspace/antigravity-swarm",
                pendingApproval = null,
                tokenUsage = TokenUsage(
                    inputTokens = 91200,
                    outputTokens = 15300,
                    estimatedCostUsd = 0.12
                ),
                subagentsCount = 0,
                isPrimary = false
            )
        )
    )
    override val sessions: Flow<List<DesktopSession>> = _sessions.asStateFlow()

    private val _activeSessionId = MutableStateFlow("desktop-main")
    private val _activeSession = MutableStateFlow<DesktopSession?>(_sessions.value.firstOrNull())
    override val activeSession: Flow<DesktopSession?> = _activeSession.asStateFlow()

    private val _logs = MutableStateFlow<List<TerminalLogEntry>>(
        listOf(
            TerminalLogEntry("01:39:12", "INFO", "Antigravity daemon connected via secure WebSocket channel"),
            TerminalLogEntry("01:39:15", "AGENT", "Received user prompt: 'Configure companion app & CI pipeline'"),
            TerminalLogEntry("01:39:18", "TOOL", "Tool invoked: run_command [CommandLine='android info']"),
            TerminalLogEntry("01:39:24", "TOOL", "Tool output: Android SDK 37 detected; OpenJDK 21 LTS active"),
            TerminalLogEntry("01:39:45", "AGENT", "Synthesized project structure. Requesting permission to build APK..."),
            TerminalLogEntry("01:40:02", "WARN", "Awaiting approval: run_command [CommandLine='git push origin main']")
        )
    )
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
            name = name,
            hostUrl = host,
            status = SessionStatus.IDLE,
            activeTask = "Newly paired session",
            currentWorkspace = "Default Workspace",
            tokenUsage = TokenUsage(0, 0, 0.0),
            isPrimary = false
        )
        _sessions.update { it + newSession }
    }
}
