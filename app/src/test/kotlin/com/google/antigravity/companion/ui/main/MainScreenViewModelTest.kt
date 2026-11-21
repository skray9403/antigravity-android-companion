package com.google.antigravity.companion.ui.main

import com.google.antigravity.companion.CompanionNavKey
import com.google.antigravity.companion.data.DefaultDataRepository
import com.google.antigravity.companion.data.SessionStatus
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class MainScreenViewModelTest {

    @Test
    fun uiState_initiallyLoadsSessionsAndDefaultTab() = runTest {
        val repository = DefaultDataRepository()
        val viewModel = MainScreenViewModel(repository)

        val state = viewModel.uiState.first()
        assertEquals(CompanionNavKey.WebPortal, state.currentTab)
        assertNotNull(state.activeSession)
        assertEquals("Studio Workstation", state.activeSession?.name)
        assertEquals(3, state.sessions.size)
    }

    @Test
    fun selectTab_updatesCurrentTab() = runTest {
        val repository = DefaultDataRepository()
        val viewModel = MainScreenViewModel(repository)

        viewModel.selectTab(CompanionNavKey.DesktopRemote)
        assertEquals(CompanionNavKey.DesktopRemote, viewModel.uiState.first().currentTab)

        viewModel.selectTab(CompanionNavKey.Settings)
        assertEquals(CompanionNavKey.Settings, viewModel.uiState.first().currentTab)
    }

    @Test
    fun approveTool_resolvesPendingApprovalAndUpdatesStatus() = runTest {
        val repository = DefaultDataRepository()
        val viewModel = MainScreenViewModel(repository)

        val initialSession = viewModel.uiState.first().activeSession
        assertNotNull(initialSession?.pendingApproval)

        val reqId = initialSession!!.pendingApproval!!.id
        viewModel.approveTool(initialSession.id, reqId)

        val updatedSession = viewModel.uiState.first().activeSession
        assertNull(updatedSession?.pendingApproval)
        assertEquals(SessionStatus.RUNNING, updatedSession?.status)
    }

    @Test
    fun denyTool_cancelsPendingApproval() = runTest {
        val repository = DefaultDataRepository()
        val viewModel = MainScreenViewModel(repository)

        val initialSession = viewModel.uiState.first().activeSession
        assertNotNull(initialSession?.pendingApproval)

        val reqId = initialSession!!.pendingApproval!!.id
        viewModel.denyTool(initialSession.id, reqId)

        val updatedSession = viewModel.uiState.first().activeSession
        assertNull(updatedSession?.pendingApproval)
        assertEquals(SessionStatus.IDLE, updatedSession?.status)
    }
}
