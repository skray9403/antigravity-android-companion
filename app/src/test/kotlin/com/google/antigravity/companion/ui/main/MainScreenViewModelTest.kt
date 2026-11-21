package com.google.antigravity.companion.ui.main

import com.google.antigravity.companion.data.DefaultDataRepository
import com.google.antigravity.companion.data.SessionStatus
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class MainScreenViewModelTest {

    @Test
    fun repository_loadsInitialSessionsAndActiveSession() = runTest {
        val repository = DefaultDataRepository()

        val sessions = repository.sessions.first()
        val active = repository.activeSession.first()

        assertEquals(3, sessions.size)
        assertNotNull(active)
        assertEquals("Studio Workstation", active?.name)
        assertEquals(SessionStatus.WAITING_APPROVAL, active?.status)
    }

    @Test
    fun repository_approvesPendingToolExecution() = runTest {
        val repository = DefaultDataRepository()
        val active = repository.activeSession.first()
        assertNotNull(active?.pendingApproval)

        val reqId = active!!.pendingApproval!!.id
        repository.approvePendingTool(active.id, reqId)

        val updated = repository.activeSession.first()
        assertNull(updated?.pendingApproval)
        assertEquals(SessionStatus.RUNNING, updated?.status)
        assertTrue(updated?.activeTask?.contains("Approved") == true)
    }

    @Test
    fun repository_deniesPendingToolExecution() = runTest {
        val repository = DefaultDataRepository()
        val active = repository.activeSession.first()
        assertNotNull(active?.pendingApproval)

        val reqId = active!!.pendingApproval!!.id
        repository.denyPendingTool(active.id, reqId)

        val updated = repository.activeSession.first()
        assertNull(updated?.pendingApproval)
        assertEquals(SessionStatus.IDLE, updated?.status)
    }

    @Test
    fun repository_sendsPromptAndUpdatesTask() = runTest {
        val repository = DefaultDataRepository()
        val active = repository.activeSession.first()
        assertNotNull(active)

        repository.sendPromptToDesktop(active!!.id, "Run security audit")
        val updated = repository.activeSession.first()
        assertEquals(SessionStatus.RUNNING, updated?.status)
        assertTrue(updated?.activeTask?.contains("Run security audit") == true)
    }

    @Test
    fun repository_providesQuickCommands() {
        val repository = DefaultDataRepository()
        assertEquals(8, repository.quickCommands.size)
        assertTrue(repository.quickCommands.any { it.command == "/goal" })
        assertTrue(repository.quickCommands.any { it.command == "/plan" })
    }
}
