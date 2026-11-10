package com.falcor.launcher

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
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

@OptIn(ExperimentalTextApi::class)
private val FalCorDisplayFont = FontFamily(
    Font(
        R.font.orbitron,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(400)
        )
    ),
    Font(
        R.font.orbitron,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(500)
        )
    ),
    Font(
        R.font.orbitron,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(600)
        )
    )
)

private val TitleShadow = Shadow(
    color = Color(0xCC327CA8),
    offset = Offset(0f, 2f),
    blurRadius = 10f
)

private val LabelShadow = Shadow(
    color = Color.Black,
    offset = Offset(0f, 2f),
    blurRadius = 5f
)

private data class Destination(
    val label: String,
    val artworkRes: Int,
    val launchPackage: String? = null,
    val hdmiPort: Int? = null,
    val collection: AppCollection? = null
)

private val destinations = listOf(
    Destination("Holodeck", R.drawable.falcor_card_holodeck,
        launchPackage = "org.jellyfin.androidtv"),
    Destination("DS9", R.drawable.falcor_card_ds9, hdmiPort = 2),
    Destination("Sport", R.drawable.falcor_card_sport,
        collection = AppCollection.SPORT),
    Destination("Free-to-Air", R.drawable.falcor_card_fta,
        collection = AppCollection.FREE_TO_AIR)
)

class MainActivity : ComponentActivity() {

    private var homeRequest by mutableStateOf(0)

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        if (
            intent.action == Intent.ACTION_MAIN &&
            intent.hasCategory(Intent.CATEGORY_HOME)
        ) {
            homeRequest++
            Log.d("FalCorNavigation", "HOME intent received")
        }
    }


    private val hdmiController by lazy {
        SonyHdmiController(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                FalCorHome(
                    homeRequest = homeRequest,
                    onHdmiSelect = { port ->
                        hdmiController.select(port)
                    },
                    onSonySettings = { onBack ->
                        hdmiController.showSettings(2, onBack)
                    }
                )
            }
        }
    }
}

@Composable
private fun FalCorHome(
    homeRequest: Int,
    onHdmiSelect: (Int) -> Unit,
    onSonySettings: (() -> Unit) -> Unit
) {
    val context = LocalContext.current

    var activeCollection by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var homeFocusIndex by rememberSaveable {
        mutableStateOf(0)
    }

    val homeFocusRequesters = remember {
        List(destinations.size) { FocusRequester() }
    }

    val settingsFocus = remember { FocusRequester() }

    var lastFocusedHomeIndex by rememberSaveable {
        mutableStateOf(0)
    }

    LaunchedEffect(homeRequest) {
        if (homeRequest > 0) {
            activeCollection = null
        }
    }

    BackHandler(enabled = activeCollection != null) {
        activeCollection = null
    }

    LaunchedEffect(activeCollection, homeFocusIndex) {
        if (activeCollection == null) {
            homeFocusRequesters[homeFocusIndex].requestFocus()
        }
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

        // Select the asset matching the actual UI surface; avoid decoding
        // a 4K bitmap on TVs rendering their UI at 1080p.
        val widthPx = with(LocalDensity.current) { screenWidth.toPx() }
        val backgroundRes = if (widthPx >= 3000f) {
            R.drawable.falcor_background_4k
        } else {
            R.drawable.falcor_background_hd
        }
        Image(
            painter = painterResource(backgroundRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        // Native, restrained LCARS accents and header.
        Box(
            modifier = Modifier
                .offset(x = screenWidth * 0.055f, y = screenHeight * 0.031f)
                .size(width = screenWidth * 0.89f, height = 2.dp)
                .background(Brush.horizontalGradient(listOf(Amber, Blue, Color.Transparent)))
        )
        Row(
            modifier = Modifier.offset(
                x = screenWidth * 0.055f, y = screenHeight * 0.051f
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "FalCor",
                color = Color(0xFFF1F0E9),
                fontSize = (screenWidth.value * 0.022f).sp,
                fontFamily = FalCorDisplayFont,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.8.sp,
                style = TextStyle(shadow = TitleShadow)
            )
            Text(
                text = "HOME ENTERTAINMENT INTERFACE",
                color = Color(0xFFA8C3D5),
                fontSize = (screenWidth.value * 0.0092f).sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Light,
                letterSpacing = 2.sp
            )
        }
        Box(
            modifier = Modifier
                .offset(x = screenWidth * 0.818f, y = screenHeight * 0.047f)
                .size(width = screenWidth * 0.081f, height = screenHeight * 0.054f),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = clock,
                color = Color(0xFFF1F0E9),
                fontSize = (screenWidth.value * 0.0145f).sp,
                fontFamily = FalCorDisplayFont,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.5.sp,
                style = TextStyle(shadow = LabelShadow)
            )
        }
        Box(
            modifier = Modifier
                .offset(x = screenWidth * 0.055f, y = screenHeight * 0.943f)
                .size(width = screenWidth * 0.89f, height = 3.dp)
                .background(Brush.horizontalGradient(listOf(Blue, Color(0xFF13263A), Amber, Blue)))
        )

        // Native, independently focusable destination cards.
        if (activeCollection == null) {

            // Interactive overlay for the settings gear
            // embedded in the approved concept artwork.
            val gearShape = RoundedCornerShape(10.dp)

            Button(
                onClick = { showMaintenanceMenu(context, onSonySettings) },
                modifier = Modifier
                    .offset(
                        x = screenWidth * 0.91f,
                        y = screenHeight * 0.042f
                    )
                    .size(
                        width = screenWidth * 0.035f,
                        height = screenHeight * 0.062f
                    )
                    .focusRequester(settingsFocus)
                    .focusProperties {
                        down = homeFocusRequesters[lastFocusedHomeIndex]
                    }
                    .semantics {
                        contentDescription = "Maintenance"
                    },
                contentPadding = PaddingValues(0.dp),
                shape = ButtonDefaults.shape(shape = gearShape),
                colors = ButtonDefaults.colors(
                    containerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    pressedContainerColor = Color.Transparent
                ),
                scale = ButtonDefaults.scale(
                    focusedScale = 1f,
                    pressedScale = 1f
                ),
                border = ButtonDefaults.border(
                    border = Border(
                        border = BorderStroke(
                            1.dp,
                            Color.Transparent
                        ),
                        shape = gearShape
                    ),
                    focusedBorder = Border(
                        border = BorderStroke(2.dp, Amber),
                        shape = gearShape
                    )
                ),
                glow = ButtonDefaults.glow(
                    glow = Glow.None,
                    focusedGlow = Glow.None
                )
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(R.drawable.falcor_settings),
                        contentDescription = null,
                        modifier = Modifier.size((screenWidth.value * 0.018f).dp),
                        colorFilter = ColorFilter.tint(Color(0xFF9BD7E8))
                    )
                }
            }

            destinations.forEachIndexed { index, destination ->

                // 85% of screen width, with evenly distributed cards.
                val modifier = Modifier
                    .offset(
                        x = screenWidth * (0.075f + index * 0.22933f),
                        y = screenHeight * 0.797f
                    )
                    .size(
                        width = screenWidth * 0.162f,
                        height = screenHeight * 0.122f
                    )
                    .focusRequester(homeFocusRequesters[index])
                    .focusProperties {
                        up = settingsFocus
                    }
                    .onFocusChanged {
                        if (it.isFocused) {
                            lastFocusedHomeIndex = index
                        }
                    }

                DestinationCard(
                    destination = destination,
                    labelSize = (screenWidth.value * 0.011f).sp,
                    modifier = modifier,
                    onLongClick = destination.collection?.let { collection ->
                        {
                            CollectionManager(context, collection).show()
                        }
                    },
                    onClick = {
                        val packageName = destination.launchPackage

                        if (packageName != null) {
                            if (!launchTvApp(context, packageName)) {
                                Toast.makeText(
                                    context,
                                    "${destination.label} is unavailable",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else if (destination.hdmiPort != null) {
                            onHdmiSelect(destination.hdmiPort)
                        } else if (destination.collection != null) {
                            homeFocusIndex = index
                            activeCollection = destination.collection.name
                        } else {
                            Toast.makeText(
                                context,
                                "${destination.label} selected",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
            }
        } else {
            FalCorAppMenu(
                collection = AppCollection.valueOf(
                    requireNotNull(activeCollection)
                ),
                modifier = Modifier
                    .offset(
                        x = screenWidth * (118f / DESIGN_WIDTH),
                        y = screenHeight * 0.685f
                    )
                    .size(
                        width = screenWidth * (1470f / DESIGN_WIDTH),
                        height = screenHeight * 0.205f
                    )
            )
        }
    }
}

@Composable
private fun DestinationCard(
    destination: Destination,
    labelSize: TextUnit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(10.dp)
    val normalBorder = Border(
        border = BorderStroke(1.dp, Color(0xFF42647D)),
        shape = shape
    )
    val focusedBorder = Border(border = BorderStroke(2.dp, Amber), shape = shape)

    Button(
        onClick = onClick,
        onLongClick = onLongClick,
        modifier = modifier,
        contentPadding = PaddingValues(0.dp),
        shape = ButtonDefaults.shape(shape = shape),
        colors = ButtonDefaults.colors(
            containerColor = SpaceBlack,
            focusedContainerColor = SpaceBlack,
            pressedContainerColor = SpaceBlack
        ),
        scale = ButtonDefaults.scale(
            focusedScale = 1.025f,
            pressedScale = 0.98f
        ),
        border = ButtonDefaults.border(
            border = normalBorder,
            focusedBorder = focusedBorder,
            pressedBorder = focusedBorder
        ),
        glow = ButtonDefaults.glow(
            glow = Glow.None,
            focusedGlow = Glow(elevationColor = Amber, elevation = 9.dp)
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(destination.artworkRes),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxWidth(0.68f)
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier.fillMaxSize().background(
                    Brush.horizontalGradient(
                        0f to Color(0xFF101F30),
                        0.30f to Color(0xFF0D1928),
                        0.58f to SpaceBlack.copy(alpha = 0.72f),
                        1f to Color.Transparent
                    )
                )
            )
            // Soft surface lighting gives the card depth.
            Box(
                modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        0.00f to Color(0x407BB9DC),
                        0.045f to Color(0x187BB9DC),
                        0.35f to Color.Transparent,
                        0.65f to Color.Transparent,
                        1.00f to Color(0x99000000)
                    )
                )
            )

            Text(
                text = destination.label.uppercase(),
                color = Color(0xFFF1F0E9),
                fontSize = labelSize,
                fontFamily = FalCorDisplayFont,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.6.sp,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(0f, 3f),
                        blurRadius = 7f
                    )
                ),
                maxLines = 1,
                modifier = Modifier.align(Alignment.CenterStart).padding(horizontal = 10.dp)
            )
        }
    }
}
