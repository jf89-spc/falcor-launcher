package com.falcor.launcher

import android.content.Context
import android.widget.Toast

/**
 * Browses installed Android TV applications using
 * the existing application catalogue and launcher.
 */
internal fun showAllAppsMenu(
    context: Context,
    onBack: () -> Unit
) {
    val apps = AppCatalog(context).discover()

    val duplicateLabels = apps
        .groupingBy { it.label }
        .eachCount()

    val actions: List<Pair<String, () -> Unit>> = apps.map { app ->
        val label = if ((duplicateLabels[app.label] ?: 0) > 1) {
            "${app.label} (${app.packageName})"
        } else {
            app.label
        }

        label to {
            if (!launchTvApp(context, app.packageName)) {
                Toast.makeText(
                    context,
                    "${app.label} is unavailable",
                    Toast.LENGTH_SHORT
                ).show()

                showAllAppsMenu(context, onBack)
            }
        }
    }

    FalCorMenuDialog.show(
        context = context,
        title = "ALL APPS",
        subtitle = "FALCOR / APPLICATIONS",
        actions = actions,
        onBack = onBack,
        message = if (apps.isEmpty()) {
            "No Android TV applications found."
        } else {
            null
        }
    )
}
