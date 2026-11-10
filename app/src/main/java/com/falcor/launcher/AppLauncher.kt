package com.falcor.launcher

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent

internal fun launchTvApp(
    context: Context,
    packageName: String
): Boolean {
    return try {
        val intent = context.packageManager
            .getLeanbackLaunchIntentForPackage(packageName)
            ?: return false

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        true
    } catch (_: ActivityNotFoundException) {
        false
    } catch (_: SecurityException) {
        false
    }
}
