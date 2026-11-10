package com.falcor.launcher

import android.app.AlertDialog
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
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

// Temporary test dialog.
// Replace with secure configuration in the maintenance interface
// after on-device HDMI switching is verified.
internal fun showSonyHdmiTest(
    activity: ComponentActivity,
    port: Int
) {
    val input = EditText(activity).apply {
        hint = "Sony pre-shared key"
        setSingleLine(true)
        inputType = InputType.TYPE_CLASS_TEXT or
            InputType.TYPE_TEXT_VARIATION_PASSWORD
        imeOptions = EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING
        importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO
        isSaveEnabled = false
    }

    val dialog = AlertDialog.Builder(activity)
        .setTitle("DS9 — HDMI $port")
        .setMessage("Enter the Sony TV pre-shared key.")
        .setView(input)
        .setNegativeButton("Cancel", null)
        .setPositiveButton("Switch") { _, _ ->

            val psk = input.text.toString()
            input.text.clear()

            if (psk.isBlank()) {
                Toast.makeText(
                    activity,
                    "Pre-shared key required",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                activity.lifecycleScope.launch {
                    val error = SonyHdmiSwitcher().switchTo(port, psk)

                    Toast.makeText(
                        activity,
                        error ?: "HDMI $port request accepted",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        .create()

    dialog.setOnDismissListener { input.text.clear() }
    dialog.show()
}
