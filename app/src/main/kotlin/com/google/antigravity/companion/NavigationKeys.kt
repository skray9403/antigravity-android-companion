package com.google.antigravity.companion

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface CompanionNavKey : NavKey {
    @Serializable
    data object WebPortal : CompanionNavKey

    @Serializable
    data object DesktopRemote : CompanionNavKey

    @Serializable
    data object Settings : CompanionNavKey
}
