package com.falcor.launcher

import android.accessibilityservice.AccessibilityService
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent

class FalCorHomeService : AccessibilityService() {

    companion object {
        private const val TAG = "FalCorHome"

        private const val PREFS = "falcor_home_service"
        private const val LAST_BOOT_COUNT = "last_boot_count"
        private const val STARTUP_WINDOW_MS = 180_000L
    }

    private val handler = Handler(Looper.getMainLooper())

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.i(TAG, "Direct HOME interception connected")
        handleBootStartup()
    }

    private fun handleBootStartup() {
        val bootCount = try {
            Settings.Global.getInt(
                contentResolver,
                Settings.Global.BOOT_COUNT,
                -1
            )
        } catch (e: SecurityException) {
            Log.w(TAG, "Unable to read boot count", e)
            return
        }

        if (bootCount < 0) {
            Log.w(TAG, "Boot count unavailable; startup skipped")
            return
        }

        val prefs = getSharedPreferences(PREFS, MODE_PRIVATE)
        val previousBoot = prefs.getInt(LAST_BOOT_COUNT, -1)

        if (bootCount == previousBoot) {
            Log.d(TAG, "Boot $bootCount already handled")
            return
        }

        prefs.edit()
            .putInt(LAST_BOOT_COUNT, bootCount)
            .apply()

        // Establish a baseline on installation, without
        // interrupting the current viewing session.
        if (previousBoot < 0) {
            Log.i(TAG, "Boot baseline recorded: $bootCount")
            return
        }

        // Do not interrupt normal viewing if the service
        // first connects long after the device has booted.
        if (SystemClock.elapsedRealtime() > STARTUP_WINDOW_MS) {
            Log.i(TAG, "Boot $bootCount: startup window expired")
            return
        }

        Log.i(
            TAG,
            "New boot detected: $previousBoot -> $bootCount"
        )

        handler.post {
            openFalCorHome("Boot startup")
        }
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode != KeyEvent.KEYCODE_HOME) {
            return false
        }

        // Consume both press and release. Launch once on release.
        if (
            event.action == KeyEvent.ACTION_UP &&
            !event.isCanceled
        ) {
            handler.post {
                openFalCorHome()
            }
        }

        return true
    }

    private fun openFalCorHome(reason: String = "HOME consumed") {
        val intent = Intent(this, MainActivity::class.java).apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_HOME)

            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            )
        }

        try {
            startActivity(intent)
            Log.i(TAG, "$reason; FalCor launch requested")
        } catch (e: ActivityNotFoundException) {
            Log.e(TAG, "FalCor HOME activity unavailable", e)
        } catch (e: SecurityException) {
            Log.e(TAG, "FalCor HOME launch denied", e)
        }
    }

    override fun onAccessibilityEvent(
        event: AccessibilityEvent?
    ) {
        // Direct key interception does not require window events.
    }

    override fun onInterrupt() {
        handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}
