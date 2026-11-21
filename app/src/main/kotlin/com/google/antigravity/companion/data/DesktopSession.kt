package com.google.antigravity.companion.data

import kotlinx.serialization.Serializable

enum class SessionStatus {
    RUNNING,
    IDLE,
    WAITING_APPROVAL,
    ERROR,
    DISCONNECTED
}

enum class RiskLevel {
    LOW,
    MODERATE,
    CRITICAL
}

@Serializable
data class ToolApprovalRequest(
    val id: String,
    val toolName: String,
    val commandOrDetails: String,
    val riskLevel: RiskLevel = RiskLevel.MODERATE,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class TokenUsage(
    val inputTokens: Int,
    val outputTokens: Int,
    val estimatedCostUsd: Double
)

@Serializable
data class DesktopSession(
    val id: String,
    val name: String,
    val hostUrl: String,
    val status: SessionStatus,
    val activeTask: String,
    val currentWorkspace: String,
    val pendingApproval: ToolApprovalRequest? = null,
    val tokenUsage: TokenUsage,
    val subagentsCount: Int = 0,
    val lastPingMs: Long = System.currentTimeMillis(),
    val isPrimary: Boolean = false
)

data class QuickSlashCommand(
    val command: String,
    val title: String,
    val description: String,
    val category: String,
    val iconName: String
)

data class TerminalLogEntry(
    val timestamp: String,
    val level: String, // "INFO", "WARN", "TOOL", "AGENT"
    val message: String
)
