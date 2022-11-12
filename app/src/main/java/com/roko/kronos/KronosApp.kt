package com.roko.kronos

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.roko.kronos.ui.theme.KronosTheme
import com.roko.kronos.ui.theme.likeBackground
import com.roko.kronos.ui.view.HomeView
import com.roko.kronos.ui.view.SettingsView

enum class Routes {
    HOME,
    SETTINGS,
}

@Composable fun KronosApp(
    context: Context,
    toClockSettingsAction: () -> Unit,
) {
    val navController = rememberNavController()

    KronosTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = likeBackground()
        ) {
            NavHost(
                navController = navController,
                startDestination = Routes.HOME.name
            ) {
                composable(route = Routes.HOME.name) {
                    HomeView(navController = navController, context = context, toClockSettingsAction = toClockSettingsAction)
                }
                composable(route = Routes.SETTINGS.name) {
                    SettingsView(navController = navController)
                }
            }
        }
    }
}
