package com.falcor.launcher

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast

internal fun showMaintenanceMenu(
    context: Context,
    onSonySettings: (() -> Unit) -> Unit
) {
    val actions = listOf<Pair<String, () -> Unit>>(
        "ALL APPS" to {
            showAllAppsMenu(context) {
                showMaintenanceMenu(context, onSonySettings)
            }
        },
        "MANAGE SPORT" to {
            CollectionManager(
                context,
                AppCollection.SPORT,
                onClose = { showMaintenanceMenu(context, onSonySettings) }
            ).show()
        },
        "MANAGE FREE-TO-AIR" to {
            CollectionManager(
                context,
                AppCollection.FREE_TO_AIR,
                onClose = { showMaintenanceMenu(context, onSonySettings) }
            ).show()
        },
        "SONY HDMI SETTINGS" to {
            onSonySettings {
                showMaintenanceMenu(context, onSonySettings)
            }
        },
        "ANDROID TV SETTINGS" to {
            try {
                context.startActivity(Intent(Settings.ACTION_SETTINGS))
            } catch (_: ActivityNotFoundException) {
                Toast.makeText(
                    context,
                    "Android TV settings unavailable",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (_: SecurityException) {
                Toast.makeText(
                    context,
                    "Unable to open Android TV settings",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    )

    FalCorMenuDialog.show(
        context = context,
        title = "MAINTENANCE",
        actions = actions,
        subtitle = "FALCOR / SYSTEMS"
    )
}
