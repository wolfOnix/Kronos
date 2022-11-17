package com.roko.kronos.ui.view

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
import com.roko.kronos.util.State.isNetworkConnection
import com.roko.kronos.util.TimeProcessor.deviceTimeMillis
import com.roko.kronos.util.TimeProcessor.networkTimeMillis
import com.roko.kronos.util.toDifferenceString
import com.roko.kronos.util.toTimeString
import kotlin.math.absoluteValue

@Composable fun HomeView(
    navController: NavController,
    context: Context,
    toClockSettingsAction: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CentredText(text = stringResource(id = R.string.hello))
        CentredText(
            text = stringResource(id = R.string.auto_time_set_is, stringResource(id = if (isAutoTimeEnabled) R.string.enabled else R.string.disabled)),
            colour = if (isAutoTimeEnabled) likeOnBackground() else Colour.RED.auto()
        )
        networkTimeMillis.let {
            // updateTime()
            Row(modifier = Modifier.padding(20.dp)) {
                Clock(titleRes = R.string.device_clock, time = deviceTimeMillis.toTimeString(context = context))
                if (isNetworkConnection) Clock(titleRes = R.string.real_clock, time = it?.toTimeString(context = context) ?: "--:--")
            }
            if (!isNetworkConnection) {
                CentredText(
                    text = stringResource(id = R.string.time_cannot_be_checked_),
                    colour = Colour.ORANGE.auto()
                )
            } else if (it != null) {
                val timeDifference = (it - deviceTimeMillis) / 1000L
                (timeDifference.absoluteValue to timeDifference.toDifferenceString()).let { (absDiffAsSec, diffString) ->
                    if (absDiffAsSec >= 10L && diffString != null) {
                        CentredText(
                            text = stringResource(
                                id = R.string.your_device_clock_is_,
                                diffString,
                                stringResource(id = if (timeDifference >= 0L) R.string.behind else R.string.ahead
                                )
                            ),
                            colour = when (absDiffAsSec) {
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
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
        CentredText(
            text = stringResource(id = R.string.to_synchronise_the_clock_, stringResource(id = if (isAutoTimeEnabled) R.string.disable_and_re_enable else R.string.enable))
        )
        CentredText(text = "* ${stringResource(id = R.string.the_option_could_be_named_differently)}", colour = Colour.GRAY.auto(), fontSizeInt = 13)
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
