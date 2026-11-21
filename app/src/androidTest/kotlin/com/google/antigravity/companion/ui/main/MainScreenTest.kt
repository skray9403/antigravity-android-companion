package com.google.antigravity.companion.ui.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.google.antigravity.companion.ui.theme.AntigravityCompanionTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/** UI tests for [com.google.antigravity.companion.ui.main.MainScreen]. */
class MainScreenTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setup() {
    composeTestRule.setContent {
      AntigravityCompanionTheme {
        MainScreen()
      }
    }
  }

  @Test
  fun bottomNavigation_tabsExist() {
    composeTestRule.onNodeWithText("Web Portal").assertExists()
    composeTestRule.onNodeWithText("Remote Hub").assertExists()
    composeTestRule.onNodeWithText("Settings").assertExists()
  }
}
