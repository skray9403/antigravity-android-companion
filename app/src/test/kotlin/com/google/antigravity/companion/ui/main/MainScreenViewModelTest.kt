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
    fun repository_startsCleanWithNoPlaceholders() = runTest {
        val repository = DefaultDataRepository()

        val sessions = repository.sessions.first()
        val active = repository.activeSession.first()
        val logs = repository.logs.first()

        assertEquals(0, sessions.size)
        assertNull(active)
        assertEquals(0, logs.size)
    }

    @Test
    fun repository_addsAndSelectsSession() = runTest {
        val repository = DefaultDataRepository()
        repository.addSession("Office PC", "http://192.168.1.50:8080")

        val sessions = repository.sessions.first()
        val active = repository.activeSession.first()

        assertEquals(1, sessions.size)
        assertNotNull(active)
        assertEquals("Office PC", active?.name)
        assertEquals("http://192.168.1.50:8080", active?.hostUrl)
        assertEquals(SessionStatus.IDLE, active?.status)
    }

    @Test
    fun repository_sendsPromptAndUpdatesTask() = runTest {
        val repository = DefaultDataRepository()
        repository.addSession("Workstation", "http://127.0.0.1:8080")
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

    @Test
    fun repository_removesSession() = runTest {
        val repository = DefaultDataRepository()
        repository.addSession("Workstation", "http://127.0.0.1:8080")
        assertEquals(1, repository.sessions.first().size)

        val id = repository.sessions.first().first().id
        repository.removeSession(id)
        assertEquals(0, repository.sessions.first().size)
        assertNull(repository.activeSession.first())
    }
}
