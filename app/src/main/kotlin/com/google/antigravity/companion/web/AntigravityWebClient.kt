package com.google.antigravity.companion.web

import android.graphics.Bitmap
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class AntigravityWebClient(
    private val onPageLoadingChanged: (Boolean) -> Unit,
    private val onErrorOccurred: (String?) -> Unit,
    private val onPageTitleChanged: (String) -> Unit = {}
) : WebViewClient() {

    companion object {
        // High-performance mobile optimization CSS injected directly into https://antigravity.google.com/
        const val MOBILE_OPTIMIZATION_CSS = """
            (function() {
                var style = document.getElementById('antigravity-mobile-optimizations');
                if (!style) {
                    style = document.createElement('style');
                    style.id = 'antigravity-mobile-optimizations';
                    style.textContent = `
                        /* Antigravity Companion Mobile Optimization Engine */
                        * {
                            -webkit-tap-highlight-color: transparent !important;
                        }
                        
                        /* Optimize scroll container for smooth mobile gestures and safe status bar area */
                        html, body {
                            overscroll-behavior-y: contain;
                            -webkit-overflow-scrolling: touch;
                        }

                        body {
                            padding-top: 40px !important;
                        }
                        
                        /* Ensure code blocks don't blow out mobile width and have horizontal scrolling */
                        pre, code {
                            max-width: 100% !important;
                            overflow-x: auto !important;
                            -webkit-overflow-scrolling: touch !important;
                            font-size: 13px !important;
                            border-radius: 8px !important;
                        }
                        
                        /* Touch-friendly buttons and interactive items */
                        button, [role="button"], a {
                            min-height: 40px !important;
                            touch-action: manipulation !important;
                        }
                        
                        /* Hide desktop scrollbars while maintaining scrolling */
                        ::-webkit-scrollbar {
                            width: 4px;
                            height: 4px;
                        }
                        ::-webkit-scrollbar-thumb {
                            background: rgba(255, 255, 255, 0.2);
                            border-radius: 4px;
                        }
                        
                        /* Responsive input adjustment for virtual keyboard */
                        textarea, input[type="text"] {
                            font-size: 16px !important; /* Prevents auto-zoom on focus */
                        }
                    `;
                    document.head.appendChild(style);
                }
                
                // Ensure viewport meta is tuned for high-DPI modern Android displays
                var meta = document.querySelector('meta[name="viewport"]');
                if (!meta) {
                    meta = document.createElement('meta');
                    meta.name = 'viewport';
                    document.head.appendChild(meta);
                }
                meta.content = 'width=device-width, initial-scale=1.0, maximum-scale=5.0, user-scalable=yes, viewport-fit=cover';
            })();
        """

        const val INJECT_SLASH_COMMAND_SCRIPT = """
            function insertSlashCommand(cmd) {
                var input = document.querySelector('textarea, [contenteditable="true"], input[type="text"]');
                if (input) {
                    input.focus();
                    if (input.tagName === 'TEXTAREA' || input.tagName === 'INPUT') {
                        input.value = cmd + ' ';
                        input.dispatchEvent(new Event('input', { bubbles: true }));
                    } else {
                        input.textContent = cmd + ' ';
                        input.dispatchEvent(new Event('input', { bubbles: true }));
                    }
                }
            }
        """
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        onPageLoadingChanged(true)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        onPageLoadingChanged(false)
        view?.title?.let { onPageTitleChanged(it) }

        // Inject the mobile optimization CSS and helper scripts
        view?.evaluateJavascript(MOBILE_OPTIMIZATION_CSS, null)
        view?.evaluateJavascript(INJECT_SLASH_COMMAND_SCRIPT, null)
    }

    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error)
        if (request?.isForMainFrame == true) {
            onPageLoadingChanged(false)
            onErrorOccurred(error?.description?.toString())
        }
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        // Keep Google Antigravity & Google Auth inside this optimized WebView
        val url = request?.url?.toString() ?: return false
        return if (url.contains("antigravity.google.com") ||
            url.contains("accounts.google.com") ||
            url.contains("google.com") ||
            url.contains("antigravity.google")
        ) {
            false // Allow WebView to load
        } else {
            false // Still keep within companion webview for smooth workflow browsing
        }
    }
}
