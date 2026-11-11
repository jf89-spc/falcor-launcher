package com.falcor.launcher

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat

/**
 * Password entry using the FalCor maintenance visual language.
 * The caller owns Sony credential validation and persistence.
 */
internal object FalCorPasswordDialog {
    private const val AMBER = 0xFFFFBD59.toInt()
    private const val WHITE = 0xFFF1F0E9.toInt()
    private const val MUTED = 0xFF9AB4C7.toInt()

    fun show(
        context: Context,
        title: String,
        message: String,
        onCancel: () -> Unit,
        onSubmit: (String) -> Unit
    ) {
        fun dp(value: Int): Int =
            (value * context.resources.displayMetrics.density + 0.5f).toInt()

        val orbitron = ResourcesCompat.getFont(context, R.font.orbitron)
        val dialog = Dialog(context, R.style.Theme_FalCor_MaintenanceDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(false)

        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(22), dp(24), dp(18))
            background = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(0xF5142A3D.toInt(), 0xF906111E.toInt())
            ).apply {
                cornerRadius = dp(16).toFloat()
                setStroke(dp(1), 0xFF40607A.toInt())
            }
        }

        fun label(
            textValue: String,
            sizeSp: Float,
            colour: Int,
            displayFont: Boolean = false
        ) = TextView(context).apply {
            text = textValue
            textSize = sizeSp
            setTextColor(colour)
            includeFontPadding = false
            if (displayFont && orbitron != null) {
                typeface = Typeface.create(orbitron, Typeface.BOLD)
            }
        }

        root.addView(label("FALCOR / SYSTEMS", 9f, MUTED).apply {
            letterSpacing = 0.22f
        })
        root.addView(label(title, 18f, WHITE, displayFont = true).apply {
            letterSpacing = 0.06f
            setPadding(0, dp(11), 0, dp(14))
        })
        root.addView(View(context).apply {
            background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(AMBER, 0xFF285C80.toInt(), Color.TRANSPARENT)
            )
        }, LinearLayout.LayoutParams(-1, dp(2)))

        val scroll = ScrollView(context).apply {
            isFillViewport = false
        }
        val content = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(3), dp(16), dp(3), dp(8))
        }
        content.addView(label(message, 13f, MUTED).apply {
            setPadding(dp(10), 0, dp(10), dp(14))
        })

        val input = EditText(context).apply {
            hint = "Sony pre-shared key"
            setSingleLine(true)
            inputType = InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_VARIATION_PASSWORD
            imeOptions = EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING or
                EditorInfo.IME_ACTION_DONE
            importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO
            isSaveEnabled = false
            setTextColor(WHITE)
            setHintTextColor(MUTED)
            textSize = 16f
            setPadding(dp(16), dp(13), dp(16), dp(13))
            background = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(0xFF162A3C.toInt(), 0xFF0C1A2A.toInt())
            ).apply {
                cornerRadius = dp(9).toFloat()
                setStroke(dp(1), 0xFF35566F.toInt())
            }
        }
        content.addView(input, LinearLayout.LayoutParams(-1, dp(55)))
        scroll.addView(content)
        root.addView(scroll)

        fun surface(focused: Boolean) = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            if (focused) {
                intArrayOf(0xFF283F4A.toInt(), 0xFF172C36.toInt())
            } else {
                intArrayOf(0xFF162A3C.toInt(), 0xFF0C1A2A.toInt())
            }
        ).apply {
            cornerRadius = dp(9).toFloat()
            setStroke(
                dp(if (focused) 2 else 1),
                if (focused) AMBER else 0xFF35566F.toInt()
            )
        }

        fun actionRow(value: String, action: () -> Unit) =
            label(value, 13f, WHITE, displayFont = true).apply {
                gravity = Gravity.CENTER
                minimumHeight = dp(51)
                isFocusable = true
                isClickable = true
                background = surface(false)
                setOnFocusChangeListener { _, focused ->
                    background = surface(focused)
                    setTextColor(if (focused) AMBER else WHITE)
                }
                setOnClickListener { action() }
            }

        var submitted = false
        val save = actionRow("SAVE AND SWITCH") {
            val psk = input.text.toString()
            if (psk.isBlank()) {
                Toast.makeText(
                    context, "Pre-shared key required", Toast.LENGTH_SHORT
                ).show()
            } else {
                submitted = true
                input.text.clear()
                dialog.dismiss()
                onSubmit(psk)
            }
        }
        root.addView(
            save,
            LinearLayout.LayoutParams(-1, dp(51)).apply {
                topMargin = dp(12)
            }
        )

        val cancel = actionRow("CANCEL") { dialog.dismiss() }
        root.addView(
            cancel,
            LinearLayout.LayoutParams(-1, dp(43)).apply {
                topMargin = dp(8)
            }
        )

        dialog.setOnDismissListener {
            input.text.clear()
            if (!submitted) onCancel()
        }

        dialog.setContentView(root)
        dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        dialog.show()
        val metrics = context.resources.displayMetrics
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                minOf(dp(570), metrics.widthPixels - dp(64)),
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            setGravity(Gravity.CENTER)
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            attributes = attributes.apply { dimAmount = 0.68f }
        }
        input.requestFocus()
    }
}
