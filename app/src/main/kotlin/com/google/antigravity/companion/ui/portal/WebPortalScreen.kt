package com.google.antigravity.companion.ui.portal

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.antigravity.companion.web.AntigravityJsBridge
import com.google.antigravity.companion.web.AntigravityWebChromeClient
import com.google.antigravity.companion.web.AntigravityWebClient

/**
 * True edge-to-edge full-bleed web portal for https://antigravity.google.com/.
 * Eliminates top app bars and redundant scaffolding so the web interface displays
 * corner-to-corner seamlessly across modern high-DPI Android displays.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebPortalScreen(
    onNavigateToRemote: () -> Unit,
    modifier: Modifier = Modifier
) {
    val portalUrl = "https://antigravity.google.com/"
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var loadingProgress by remember { mutableIntStateOf(0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var canGoBack by remember { mutableStateOf(false) }

    // Intercept back gesture to navigate web history seamlessly
    BackHandler(enabled = canGoBack) {
        webViewInstance?.let {
            if (it.canGoBack()) it.goBack()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 1. Full-Bleed Edge-to-Edge WebView (Draws directly behind transparent system bars)
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        databaseEnabled = true
                        useWideViewPort = true
                        loadWithOverviewMode = true
                        setSupportZoom(true)
                        builtInZoomControls = true
                        displayZoomControls = false
                        cacheMode = WebSettings.LOAD_DEFAULT
                        mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                        mediaPlaybackRequiresUserGesture = false
                        allowContentAccess = true
                        allowFileAccess = true
                    }

                    addJavascriptInterface(
                        AntigravityJsBridge(
                            context = ctx,
                            onApprovalDetected = { _, _ -> onNavigateToRemote() }
                        ),
                        "AntigravityNativeBridge"
                    )

                    webViewClient = AntigravityWebClient(
                        onPageLoadingChanged = { loading ->
                            isLoading = loading
                            canGoBack = canGoBack()
                        },
                        onErrorOccurred = { err ->
                            errorMessage = err ?: "Unable to connect to antigravity.google.com"
                        }
                    )

                    webChromeClient = AntigravityWebChromeClient(
                        onProgressUpdate = { progress ->
                            loadingProgress = progress
                            if (progress >= 100) isLoading = false
                        }
                    )

                    loadUrl(portalUrl)
                    webViewInstance = this
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // 2. Minimalist Non-Intrusive Loading Bar at the top edge (within status bar area)
        AnimatedVisibility(
            visible = isLoading && loadingProgress < 100,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            LinearProgressIndicator(
                progress = { loadingProgress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.Transparent
            )
        }

        // 3. Clean Error Overlay if offline / disconnected
        if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Unable to reach Antigravity",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = errorMessage ?: "Please check your network connection.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(onClick = {
                        errorMessage = null
                        webViewInstance?.loadUrl(portalUrl)
                    }) {
                        Text("Retry Connection")
                    }
                }
            }
        }
    }
}
