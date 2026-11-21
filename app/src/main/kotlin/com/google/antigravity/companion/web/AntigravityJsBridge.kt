package com.google.antigravity.companion.web

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.webkit.JavascriptInterface
import android.widget.Toast

class AntigravityJsBridge(
    private val context: Context,
    private val onApprovalDetected: (toolName: String, command: String) -> Unit = { _, _ -> },
    private val onTaskFinished: (title: String, summary: String) -> Unit = { _, _ -> }
) {

    @JavascriptInterface
    fun triggerHapticFeedback(patternType: String = "light") {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val vibrator = vibratorManager?.defaultVibrator
                val effect = when (patternType) {
                    "heavy" -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
                    "double" -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
                    else -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                }
                vibrator?.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(50)
            }
        } catch (_: Exception) {
            // Ignore vibration errors on devices without vibrator hardware
        }
    }

    @JavascriptInterface
    fun onToolApprovalRequested(toolName: String, command: String) {
        triggerHapticFeedback("heavy")
        onApprovalDetected(toolName, command)
    }

    @JavascriptInterface
    fun onAgentTaskCompleted(title: String, summary: String) {
        triggerHapticFeedback("double")
        onTaskFinished(title, summary)
    }

    @JavascriptInterface
    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
