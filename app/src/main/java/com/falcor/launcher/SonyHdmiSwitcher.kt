package com.falcor.launcher

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.Proxy
import java.net.URL

internal class SonyHdmiSwitcher {

    // Null indicates that Sony accepted the request.
    // A non-null value describes a failure.
    suspend fun switchTo(
        port: Int,
        psk: String
    ): String? = withContext(Dispatchers.IO) {

        try {
            val connection = URL(
                "http://127.0.0.1/sony/avContent"
            ).openConnection(Proxy.NO_PROXY) as HttpURLConnection

            try {
                connection.requestMethod = "POST"
                connection.connectTimeout = 3000
                connection.readTimeout = 5000
                connection.instanceFollowRedirects = false
                connection.doOutput = true

                connection.setRequestProperty(
                    "Content-Type", "application/json"
                )
                connection.setRequestProperty("X-Auth-PSK", psk)

                val payload = JSONObject()
                    .put("method", "setPlayContent")
                    .put("id", 1)
                    .put("version", "1.0")
                    .put(
                        "params",
                        JSONArray().put(
                            JSONObject().put(
                                "uri", "extInput:hdmi?port=$port"
                            )
                        )
                    )

                connection.outputStream
                    .bufferedWriter(Charsets.UTF_8)
                    .use { it.write(payload.toString()) }

                val status = connection.responseCode

                if (status != 200) {
                    return@withContext "Sony REST API returned HTTP $status"
                }

                val response = connection.inputStream
                    .bufferedReader()
                    .use { JSONObject(it.readText()) }

                when {
                    response.has("error") -> {
                        val code = response.optJSONArray("error")
                            ?.optInt(0)
                        "Sony rejected the request (code $code)"
                    }

                    response.has("result") -> null

                    else -> "Unexpected Sony REST response"
                }
            } finally {
                connection.disconnect()
            }

        } catch (_: IOException) {
            "Unable to connect to the local Sony REST API"
        } catch (_: JSONException) {
            "Invalid response from the Sony REST API"
        } catch (_: SecurityException) {
            "Android denied network access"
        }
    }
}

internal class SonyHdmiController(
    private val activity: ComponentActivity
) {
    private val store = SonyPskStore(
        activity.applicationContext
    )

    private val switcher = SonyHdmiSwitcher()
    private var busy = false

    fun select(port: Int) {
        if (busy) return
        busy = true

        activity.lifecycleScope.launch {
            val saved = withContext(Dispatchers.IO) {
                runCatching { store.read() }
            }

            val psk = saved.getOrNull()

            if (psk == null) {
                val message = if (saved.isFailure) {
                    "Saved credential unavailable. Enter the Sony PSK again."
                } else {
                    "Enter the Sony PSK. It will be saved after a successful switch."
                }

                showSetup(port, message)
                return@launch
            }

            val error = switcher.switchTo(port, psk)

            if (isAuthenticationError(error)) {
                showSetup(
                    port,
                    "The saved PSK was rejected. Enter the current Sony PSK."
                )
            } else {
                busy = false

                if (error != null) {
                    notifyUser(error)
                }
            }
        }
    }

    fun showSettings(
        port: Int,
        onBack: () -> Unit
    ) {
        if (busy || activity.isFinishing || activity.isDestroyed) {
            return
        }

        val returnToSettings = { showSettings(port, onBack) }

        FalCorMenuDialog.show(
            context = activity,
            title = "Sony HDMI settings",
            actions = listOf(
                "Configure or replace PSK" to {
                    busy = true
                    showSetup(
                        port,
                        "Enter the Sony PSK. FalCor will switch to DS9 " +
                            "to verify it before saving.",
                        onCancel = returnToSettings
                    )
                },
                "Clear saved PSK" to {
                    confirmClear(returnToSettings)
                }
            ),
            onBack = onBack
        )
    }

    private fun confirmClear(onBack: () -> Unit) {
        FalCorMenuDialog.show(
            context = activity,
            title = "Clear saved Sony PSK?",
            message = "DS9 will require the Sony PSK the next time " +
                "you select it. This does not change the " +
                "pre-shared key configured on the television.",
            actions = listOf(
                "CLEAR SAVED PSK" to {
                    busy = true

                    activity.lifecycleScope.launch {
                        try {
                            val result = withContext(Dispatchers.IO) {
                                runCatching { store.clear() }
                            }

                            if (result.isSuccess) {
                                notifyUser("Saved Sony credential cleared")
                            } else {
                                notifyUser("Unable to clear saved credential")
                            }
                        } finally {
                            busy = false
                        }

                        if (!activity.isFinishing && !activity.isDestroyed) {
                            onBack()
                        }
                    }
                }
            ),
            onBack = onBack
        )
    }

    private fun isAuthenticationError(error: String?): Boolean {
        return error == "Sony REST API returned HTTP 403" ||
            error == "Sony REST API returned HTTP 401" ||
            error == "Sony rejected the request (code 403)" ||
            error == "Sony rejected the request (code 401)"
    }

    private fun notifyUser(message: String) {
        Toast.makeText(
            activity,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showSetup(
        port: Int,
        message: String,
        onCancel: (() -> Unit)? = null
    ) {
        if (activity.isFinishing || activity.isDestroyed) {
            busy = false
            return
        }

        FalCorPasswordDialog.show(
            context = activity,
            title = "Configure DS9",
            message = message,
            onCancel = {
                busy = false
                if (!activity.isFinishing && !activity.isDestroyed) {
                    onCancel?.invoke()
                }
            },
            onSubmit = { psk ->
                activity.lifecycleScope.launch {
                    try {
                        val error = switcher.switchTo(port, psk)

                        if (error != null) {
                            notifyUser(error)
                        } else {
                            val saved = withContext(Dispatchers.IO) {
                                runCatching { store.save(psk) }
                            }

                            if (saved.isFailure) {
                                notifyUser(
                                    "HDMI switched, but credential could not be saved"
                                )
                            }
                        }
                    } finally {
                        busy = false
                    }
                }
            }
        )
    }
}
