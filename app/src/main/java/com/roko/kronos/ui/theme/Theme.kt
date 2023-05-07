package com.roko.kronos.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val LightColorScheme = lightColorScheme(
    primary = Colour.FORE.light,
    onPrimary = Colour.BACK.light,
    secondary = Colour.ORANGE.light,
    tertiary = Colour.RED.light,
    background = Colour.BACK.light,
    onBackground = Colour.FORE.light,
)

private val DarkColorScheme = darkColorScheme(
    primary = Colour.FORE.dark,
    onPrimary = Colour.BACK.dark,
    secondary = Colour.ORANGE.dark,
    tertiary = Colour.RED.dark,
    background = Colour.BACK.dark,
    onBackground = Colour.FORE.dark,
)

@Composable fun KronosTheme(
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = isSystemInDarkTheme()

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = colorScheme.background,
            darkIcons = !darkTheme
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
