package com.falcor.launcher

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Glow
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private const val DESIGN_WIDTH = 1672f
private const val DESIGN_HEIGHT = 941f

private val SpaceBlack = Color(0xFF030A11)
private val Amber = Color(0xFFFFBD59)
private val Blue = Color(0xFF285C80)

private data class Destination(
    val label: String,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
)

private val destinations = listOf(
    Destination("Holodeck", 118, 693, 344, 137),
    Destination("DS9", 490, 693, 344, 137),
    Destination("Sport", 862, 693, 341, 137),
    Destination("Free-to-Air", 1232, 693, 356, 137)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                FalCorHome()
            }
        }
    }
}

@Composable
private fun FalCorHome() {
    val context = LocalContext.current
    val artwork = ImageBitmap.imageResource(
        R.drawable.falcor_concept
    )

    val firstFocus = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        firstFocus.requestFocus()
    }

    val clockFormat = remember {
        DateTimeFormatter.ofPattern("HH:mm")
    }

    var clock by remember {
        mutableStateOf(LocalTime.now().format(clockFormat))
    }

    LaunchedEffect(Unit) {
        while (true) {
            clock = LocalTime.now().format(clockFormat)
            delay(1000)
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        // Original cinematic composition.
        Image(
            bitmap = artwork,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // Cover the original static card row.
        // Retain the LCARS frame and surrounding artwork.
        Box(
            modifier = Modifier
                .offset(
                    x = screenWidth * 0.04f,
                    y = screenHeight * 0.70f
                )
                .size(
                    width = screenWidth * 0.94f,
                    height = screenHeight * 0.225f
                )
                .background(
                    Brush.verticalGradient(
                        0.00f to Color.Transparent,
                        0.10f to SpaceBlack,
                        0.90f to SpaceBlack,
                        1.00f to Color.Transparent
                    )
                )
        )

        // Replace the clock embedded in the concept.
        Box(
            modifier = Modifier
                .offset(
                    x = screenWidth * 0.835f,
                    y = screenHeight * 0.035f
                )
                .size(
                    width = screenWidth * 0.09f,
                    height = screenHeight * 0.055f
                )
                .background(Color(0xFF04090E)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = clock,
                color = Color(0xFFF1F0E9),
                fontSize = (screenWidth.value * 0.018f).sp
            )
        }

        // Native, independently focusable destination cards.
        destinations.forEachIndexed { index, destination ->

            val modifier = Modifier
                .offset(
                    x = screenWidth *
                        (destination.x / DESIGN_WIDTH),
                    y = screenHeight *
                        (destination.y / DESIGN_HEIGHT)
                )
                .size(
                    width = screenWidth *
                        (destination.width / DESIGN_WIDTH),
                    height = screenHeight *
                        (destination.height / DESIGN_HEIGHT)
                )
                .then(
                    if (index == 0) {
                        Modifier.focusRequester(firstFocus)
                    } else {
                        Modifier
                    }
                )

            DestinationCard(
                destination = destination,
                artwork = artwork,
                modifier = modifier,
                onClick = {
                    Toast.makeText(
                        context,
                        "${destination.label} selected",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }
}

@Composable
private fun DestinationCard(
    destination: Destination,
    artwork: ImageBitmap,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)

    // Crop inside the original card border so the
    // amber highlight belongs to the focused button.
    val painter = remember(artwork, destination) {
        BitmapPainter(
            image = artwork,
            srcOffset = IntOffset(
                destination.x + 7,
                destination.y + 7
            ),
            srcSize = IntSize(
                destination.width - 14,
                destination.height - 14
            )
        )
    }

    val normalBorder = Border(
        border = BorderStroke(1.dp, Blue),
        shape = shape
    )

    val focusedBorder = Border(
        border = BorderStroke(2.dp, Amber),
        shape = shape
    )

    Button(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(0.dp),
        shape = ButtonDefaults.shape(
            shape = shape
        ),
        colors = ButtonDefaults.colors(
            containerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            pressedContainerColor = Color.Transparent
        ),
        scale = ButtonDefaults.scale(
            focusedScale = 1f,
            pressedScale = 0.98f
        ),
        border = ButtonDefaults.border(
            border = normalBorder,
            focusedBorder = focusedBorder,
            pressedBorder = focusedBorder
        ),
        glow = ButtonDefaults.glow(
            glow = Glow.None,
            focusedGlow = Glow(
                elevationColor = Amber,
                elevation = 12.dp
            )
        )
    ) {
        Image(
            painter = painter,
            contentDescription = destination.label,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}
