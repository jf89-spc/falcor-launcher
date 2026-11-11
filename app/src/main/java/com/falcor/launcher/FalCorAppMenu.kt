package com.falcor.launcher

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.tv.material3.Border
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Glow
import androidx.tv.material3.Text

private val MenuFont = FontFamily(Font(R.font.orbitron))

@Composable
internal fun FalCorAppMenu(
    collection: AppCollection,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val store = remember(context) {
        AppCollectionStore(context)
    }

    var entries by remember(collection) {
        mutableStateOf(store.load(collection))
    }

    val listState = rememberLazyListState()

    val installedPackages = remember(collection) {
        AppCatalog(context).discover()
            .map { it.packageName }
            .toSet()
    }

    val title = when (collection) {
        AppCollection.SPORT -> "SPORT"
        AppCollection.FREE_TO_AIR -> "FREE-TO-AIR"
    }

    val firstFocus = remember(collection) {
        FocusRequester()
    }

    var focusPackage by remember(collection) {
        mutableStateOf(entries.firstOrNull()?.packageName)
    }

    var focusRequestId by remember(collection) {
        mutableStateOf(0)
    }

    var completedFocusRequest by remember(collection) {
        mutableStateOf(-1)
    }

    LaunchedEffect(collection, focusRequestId) {
        val index = entries.indexOfFirst {
            it.packageName == focusPackage
        }

        if (
            index >= 0 &&
            listState.layoutInfo.visibleItemsInfo.none {
                it.index == index
            }
        ) {
            listState.scrollToItem(index)
        }
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = Color(0xFFFFBD59),
                fontSize = 16.sp,
                fontFamily = MenuFont,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color(0xB3000000),
                        offset = Offset(0f, 2f),
                        blurRadius = 5f
                    )
                )
            )

            Text(
                text = "BACK  /  HOME",
                color = Color(0xFF9AB4C7),
                fontSize = 10.sp,
                letterSpacing = 0.6.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (entries.isEmpty()) {
            Text(
                text = "No applications configured",
                color = Color(0xFF9AB4C7),
                fontSize = 12.sp
            )
        } else {
            LazyRow(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(
                    horizontal = 8.dp,
                    vertical = 5.dp
                ),
                horizontalArrangement = if (entries.size <= 5) {
                    Arrangement.SpaceEvenly
                } else {
                    Arrangement.spacedBy(12.dp)
                }
            ) {
                itemsIndexed(
                    items = entries,
                    key = { _, entry -> entry.packageName }
                ) { index, entry ->

                    val available =
                        entry.packageName in installedPackages

                    if (entry.packageName == focusPackage) {
                        LaunchedEffect(collection, focusRequestId) {
                            if (completedFocusRequest != focusRequestId) {
                                firstFocus.requestFocus()
                                completedFocusRequest = focusRequestId
                            }
                        }
                    }

                    CollectionAppCard(
                        entry = entry,
                        available = available,
                        modifier = Modifier
                            .width(116.dp)
                            .fillMaxHeight()
                            .then(
                                if (entry.packageName == focusPackage) {
                                    Modifier.focusRequester(firstFocus)
                                } else {
                                    Modifier
                                }
                            ),
                        onLongClick = {
                            CollectionManager(context, collection)
                                .showEntry(entry) {
                                    val updated = store.load(collection)

                                    focusPackage = updated
                                        .firstOrNull {
                                            it.packageName == entry.packageName
                                        }
                                        ?.packageName
                                        ?: updated.getOrNull(
                                            index.coerceAtMost(
                                                updated.lastIndex
                                            )
                                        )?.packageName

                                    entries = updated
                                    focusRequestId++
                                }
                        },
                        onClick = {
                            if (
                                !available ||
                                !launchTvApp(
                                    context,
                                    entry.packageName
                                )
                            ) {
                                Toast.makeText(
                                    context,
                                    "${entry.label} is unavailable",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CollectionAppCard(
    entry: AppEntry,
    available: Boolean,
    modifier: Modifier = Modifier,
    onLongClick: () -> Unit,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    val icon = remember(entry.packageName, available) {
        if (available) {
            runCatching {
                context.packageManager
                    .getApplicationIcon(entry.packageName)
                    .toBitmap(96, 96)
                    .asImageBitmap()
            }.getOrNull()
        } else {
            null
        }
    }

    val shape = RoundedCornerShape(12.dp)

    Button(
        onClick = onClick,
        onLongClick = onLongClick,
        modifier = modifier,
        contentPadding = PaddingValues(0.dp),
        shape = ButtonDefaults.shape(shape = shape),
        colors = ButtonDefaults.colors(
            containerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            pressedContainerColor = Color.Transparent
        ),
        scale = ButtonDefaults.scale(
            focusedScale = 1.025f,
            pressedScale = 0.98f
        ),
        border = ButtonDefaults.border(
            border = Border(
                border = BorderStroke(1.dp, Color(0xFF42647D)),
                shape = shape
            ),
            focusedBorder = Border(
                border = BorderStroke(2.dp, Color(0xFFFFBD59)),
                shape = shape
            )
        ),
        glow = ButtonDefaults.glow(
            glow = Glow.None,
            focusedGlow = Glow(
                elevationColor = Color(0xFFFFBD59),
                elevation = 8.dp
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.00f to Color(0xFF1A3449),
                        0.12f to Color(0xFF11283A),
                        0.62f to Color(0xFF091A2A),
                        1.00f to Color(0xFF040C15)
                    )
                )
        ) {
            // Thin reflected edge, matching the home card treatment.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0x667EB8D7))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 7.dp, vertical = 5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Image(
                        bitmap = icon,
                        contentDescription = null,
                        modifier = Modifier.size(34.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = entry.label,
                    color = Color(0xFFF1F0E9),
                    fontFamily = MenuFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    letterSpacing = 0.2.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(0f, 2f),
                            blurRadius = 5f
                        )
                    )
                )

                if (!available) {
                    Text(
                        text = "Unavailable",
                        color = Color(0xFF9AB4C7),
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}
