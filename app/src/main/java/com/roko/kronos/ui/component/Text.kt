package com.roko.kronos.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable fun CentredText(
    text: String,
    colour: Color = MaterialTheme.colorScheme.onBackground,
    fontSizeInt: Int = 16
) {
    Text(text = text, textAlign = TextAlign.Center, color = colour, fontSize = fontSizeInt.sp)
}
