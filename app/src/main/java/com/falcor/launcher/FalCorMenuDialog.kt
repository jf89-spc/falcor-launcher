package com.falcor.launcher

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat

/**
 * Focusable, remote-first FalCor menu surface.
 * Business logic stays with the caller; this class owns presentation only.
 */
internal object FalCorMenuDialog {
    private const val AMBER = 0xFFFFBD59.toInt()
    private const val WHITE = 0xFFF1F0E9.toInt()
    private const val MUTED = 0xFF9AB4C7.toInt()

    fun show(
        context: Context,
        title: String,
        actions: List<Pair<String, () -> Unit>>,
        onBack: (() -> Unit)? = null,
        subtitle: String = "FALCOR / SYSTEMS",
        message: String? = null
    ) {
        fun dp(value: Int): Int =
            (value * context.resources.displayMetrics.density + 0.5f).toInt()

        val orbitron = ResourcesCompat.getFont(context, R.font.orbitron)
        val dialog = Dialog(context, R.style.Theme_FalCor_MaintenanceDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setOnCancelListener { onBack?.invoke() }

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
            value: String,
            sizeSp: Float,
            colour: Int,
            isOrbitron: Boolean = false
        ): TextView = TextView(context).apply {
            text = value
            textSize = sizeSp
            setTextColor(colour)
            includeFontPadding = false
            if (isOrbitron) {
                typeface = Typeface.create(orbitron, Typeface.BOLD)
            }
        }

        root.addView(label(subtitle, 9f, MUTED).apply {
            letterSpacing = 0.22f
        })
        root.addView(label(title, 18f, WHITE, isOrbitron = true).apply {
            letterSpacing = 0.06f
            setPadding(0, dp(11), 0, dp(14))
        })
        root.addView(View(context).apply {
            background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(AMBER, 0xFF285C80.toInt(), Color.TRANSPARENT)
            )
        }, LinearLayout.LayoutParams(-1, dp(2)))

        fun surface(focused: Boolean): GradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            if (focused) {
                intArrayOf(0xFF283F4A.toInt(), 0xFF172C36.toInt())
            } else {
                intArrayOf(0xFF162A3C.toInt(), 0xFF0C1A2A.toInt())
            }
        ).apply {
            cornerRadius = dp(9).toFloat()
            setStroke(dp(if (focused) 2 else 1),
                if (focused) AMBER else 0xFF35566F.toInt())
        }

        fun actionRow(text: String, action: () -> Unit): TextView =
            label(text, 13f, WHITE, isOrbitron = true).apply {
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(18), dp(8), dp(18), dp(8))
                minimumHeight = dp(55)
                isClickable = true
                isFocusable = true
                background = surface(false)
                setOnFocusChangeListener { _, focused ->
                    background = surface(focused)
                    setTextColor(if (focused) AMBER else WHITE)
                    scaleX = if (focused) 1.012f else 1f
                    scaleY = if (focused) 1.012f else 1f
                }
                setOnClickListener {
                    dialog.dismiss()
                    action()
                }
            }

        val scroll = ScrollView(context).apply {
            isFillViewport = false
            clipToPadding = false
            descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
        }
        val items = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(3), dp(12), dp(3), dp(10))
        }
        var firstAction: View? = null

        if (message != null) {
            items.addView(label(message, 13f, MUTED).apply {
                setPadding(dp(12), dp(12), dp(12), dp(18))
            })
        }

        actions.forEach { (text, action) ->
            val row = actionRow(text, action)
            items.addView(row, LinearLayout.LayoutParams(-1, dp(55)).apply {
                bottomMargin = dp(8)
            })
            if (firstAction == null) firstAction = row
        }
        scroll.addView(items)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))

        val back = actionRow(if (onBack == null) "CLOSE" else "BACK", {
            onBack?.invoke()
        }).apply {
            textSize = 11f
            setTextColor(MUTED)
            gravity = Gravity.CENTER
            minimumHeight = dp(43)
        }
        root.addView(back, LinearLayout.LayoutParams(-1, dp(43)))

        dialog.setContentView(root)
        dialog.show()

        val metrics = context.resources.displayMetrics
        val width = minOf(dp(570), metrics.widthPixels - dp(64))
        val desiredHeight = dp(205 + actions.size * 63 + if (message != null) 55 else 0)
        val height = minOf(desiredHeight, (metrics.heightPixels * 0.82f).toInt())
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(width, height)
            setGravity(Gravity.CENTER)
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            attributes = attributes.apply { dimAmount = 0.68f }
        }
        (firstAction ?: back).requestFocus()
    }
}
