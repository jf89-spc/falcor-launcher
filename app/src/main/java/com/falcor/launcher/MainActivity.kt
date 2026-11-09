
package com.falcor.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text

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
    val destinations = listOf(
        "Holodeck",
        "DS9",
        "Sport",
        "Free-to-Air"
    )

    var selectedDestination by rememberSaveable {
        mutableStateOf("None")
    }

    val firstButtonFocus = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        firstButtonFocus.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B101B))
            .padding(horizontal = 64.dp, vertical = 48.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "FalCor",
            style = MaterialTheme.typography.displayMedium,
            color = Color.White
        )

        Text(
            text = "Home Entertainment Interface",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFFADB8C8)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            destinations.forEachIndexed { index, destination ->

                val focusModifier = if (index == 0) {
                    Modifier.focusRequester(firstButtonFocus)
                } else {
                    Modifier
                }

                Button(
                    onClick = {
                        selectedDestination = destination
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(88.dp)
                        .then(focusModifier)
                ) {
                    Text(destination)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Selected: $selectedDestination",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFFADB8C8)
        )
    }
}
