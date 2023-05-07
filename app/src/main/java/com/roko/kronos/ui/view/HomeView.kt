package com.roko.kronos.ui.view

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.roko.kronos.R
import com.roko.kronos.Routes
import com.roko.kronos.ui.component.CentredText
import com.roko.kronos.ui.component.Clock
import com.roko.kronos.ui.component.TextButton
import com.roko.kronos.ui.theme.Colour
import com.roko.kronos.ui.theme.auto
import com.roko.kronos.ui.theme.likeOnBackground
import com.roko.kronos.util.State.isAutoTimeEnabled
import com.roko.kronos.util.ClockState
import com.roko.kronos.viewmodel.ClockViewModel
import com.roko.kronos.viewmodel.ClockViewModelFactory
import kotlin.math.absoluteValue

@Composable fun HomeView(
    navController: NavController,
    context: Context,
    toClockSettingsAction: () -> Unit,
    viewModel: ClockViewModel = viewModel(factory = ClockViewModelFactory(context = context)), // would have been only "= viewModel()" (" = viewModel<ClockViewModel>()"), without the factory parameter and the factory class if the ClockVM class hadn't contained any parameter
) {
    val clockState: ClockState by viewModel.clockState.collectAsStateWithLifecycle()
    val deviceTime: String by viewModel.deviceTime.collectAsStateWithLifecycle()
    val networkTime: String? by viewModel.networkTime.collectAsStateWithLifecycle()
    val timeDelta: Pair<Long?, String?> by viewModel.timeDelta.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(modifier = Modifier.padding(20.dp)) {
            Clock(titleRes = R.string.device_clock, time = deviceTime)
            Clock(titleRes = R.string.real_clock, time = networkTime ?: "- - -")
        }
        if (clockState == ClockState.NO_INTERNET) {
            CentredText(
                text = stringResource(id = R.string.time_cannot_be_checked_),
                colour = Colour.ORANGE.auto()
            )
        } else if (networkTime != null) {
            timeDelta.let { (deltaMillis, deltaString) ->
                if (deltaMillis != null) {
                    if (deltaMillis.absoluteValue >= 10L && deltaString != null) { // todo - replace precision with user's defined precision
                        CentredText(
                            text = stringResource(
                                id = R.string.your_device_clock_is_,
                                deltaString,
                                stringResource(id = if (deltaMillis >= 0L) R.string.behind else R.string.ahead)
                            ),
                            colour = when (deltaMillis.absoluteValue) {
                                in 10L until 30L -> Colour.YELLOW.auto()
                                in 30L until 1200 -> Colour.ORANGE.auto()
                                else -> Colour.RED.auto()
                            }
                        )
                    } else {
                        CentredText(
                            text = stringResource(id = R.string.your_device_clock_is_synchronised_),
                            colour = Colour.GREEN.auto()
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
        CentredText(
            text = stringResource(id = R.string.auto_time_set_is, stringResource(id = if (isAutoTimeEnabled) R.string.enabled else R.string.disabled)),
            colour = if (isAutoTimeEnabled) likeOnBackground() else Colour.RED.auto()
        )
        Spacer(modifier = Modifier.height(20.dp))
        CentredText(
            text = stringResource(id = R.string.to_synchronise_the_clock_, stringResource(id = if (isAutoTimeEnabled) R.string.disable_and_re_enable else R.string.enable))
        )
        Spacer(modifier = Modifier.height(20.dp))
        TextButton(
            stringRes = R.string.open_time_settings,
            onClick = toClockSettingsAction
        )
        TextButton(
            stringRes = R.string.settings,
            onClick = { navController.navigate(Routes.SETTINGS.name) }
        )
    }
}
