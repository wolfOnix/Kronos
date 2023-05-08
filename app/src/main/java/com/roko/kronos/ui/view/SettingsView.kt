package com.roko.kronos.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.roko.kronos.R
import com.roko.kronos.constants.MODIFIER_PADDING_BOTTOM_STANDARD
import com.roko.kronos.globalPrefs
import com.roko.kronos.model.NotificationData
import com.roko.kronos.model.RepeatInterval
import com.roko.kronos.processor.background.WorkManagerHandler
import com.roko.kronos.processor.notification.NotificationProcessor.postNotification
import com.roko.kronos.ui.component.HeaderIcon
import com.roko.kronos.ui.component.TextButton

// todo - ensure that the Work does not remain active when notificationsEnabled is false (and false is the desired value)

@Composable fun SettingsView(navController: NavController) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 40.dp) // todo - use paddings that do not stay fixed when scrolling (that are only on the first top and last bottom - same on HomeView
            .verticalScroll(state = scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var notificationsEnabled by remember { mutableStateOf(globalPrefs.SETTING_NOTIFICATIONS_ENABLED) } // todo - if notifications are enabled, check and request permission (SDK 33+)

        HeaderIcon()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(MODIFIER_PADDING_BOTTOM_STANDARD)
        ) {
            Text(modifier = Modifier.weight(.8f), text = "Receive an alert when the device clock is out of sync")
            Box(
                modifier = Modifier.weight(.2f),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { toChecked ->
                        notificationsEnabled = !notificationsEnabled
                        globalPrefs.SETTING_NOTIFICATIONS_ENABLED = notificationsEnabled
                        if (toChecked) {
                            WorkManagerHandler.setWorkManager(repeatInterval = RepeatInterval.FIFTEEN_MINUTES) // todo - let the user choose the interval
                        } else {
                            WorkManagerHandler.cancelWorkManager()
                            // todo - clear any existing notification
                        }
                    }
                )
            }
        }
        TextButton(
            stringRes = R.string.test_notification,
            onClick = {
                postNotification(
                    NotificationData(
                        title = "Test notification",
                        preview = "Device clock out of sync...",
                        content = "The device clock is 3 years and 4 seconds behind the network clock. Tap the notification to solve the abominable issue!"
                    )
                )
            }
        )
    }
}
