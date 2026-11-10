package com.falcor.launcher

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast

internal fun showMaintenanceMenu(
    context: Context,
    onSonySettings: (() -> Unit) -> Unit
) {
    val options = arrayOf(
        "Manage Sport",
        "Manage Free-to-Air",
        "Android TV settings",
        "Sony HDMI settings"
    )

    AlertDialog.Builder(context, R.style.Theme_FalCor_MaintenanceDialog)
        .setTitle("FalCor — Maintenance")
        .setItems(options) { _, index ->
            when (index) {
                0 -> CollectionManager(
                    context,
                    AppCollection.SPORT,
                    onClose = {
                        showMaintenanceMenu(context, onSonySettings)
                    }
                ).show()

                1 -> CollectionManager(
                    context,
                    AppCollection.FREE_TO_AIR,
                    onClose = {
                        showMaintenanceMenu(context, onSonySettings)
                    }
                ).show()

                3 -> onSonySettings {
                    showMaintenanceMenu(context, onSonySettings)
                }

                2 -> {
                    try {
                        context.startActivity(
                            Intent(Settings.ACTION_SETTINGS)
                        )
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
            }
        }
        .setNegativeButton("Close", null)
        .show()
}
