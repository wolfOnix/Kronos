package com.roko.kronos.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Mint = Color(0xFF51E5FD)
private val Lavender = Color(0xFF5177FD)
private val Raisin = Color(0xFF191A55)

enum class Colour(val light: Color, val dark: Color) {
    // Background and foreground/ text
    BACK(Lavender, Raisin),
    FORE(Color(0xFFFFFFFF), Color(0xFFADADAD)),

    // Interface colours
    YELLOW(Color(0xFFFFD54F), Color(0xFFFF99AD)),
    ORANGE(Color(0xFFFFB74D), Color(0xFFFCDE9C)),
    RED(Color(0xFFFF8A65), Color(0xFFADADAD)),
    GREEN(Color(0xFF4DB6AC), Color(0xFFADADAD)),
    GRAY(Color(0xFF909090), Color(0xFFADADAD)),
}

/** @return The version of this colour that corresponds to the current light/ dark state of the device. */
@Composable fun Colour.auto(): Color = if (isSystemInDarkTheme()) this.dark else this.light

@Composable fun likeBackground() = MaterialTheme.colorScheme.background

@Composable fun likeForeground() = MaterialTheme.colorScheme.onBackground
