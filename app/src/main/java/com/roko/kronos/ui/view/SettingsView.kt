package com.roko.kronos.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.roko.kronos.R
import com.roko.kronos.globalPrefs
import com.roko.kronos.model.NotificationData
import com.roko.kronos.model.RepeatInterval
import com.roko.kronos.processor.background.WorkManagerHandler
import com.roko.kronos.ui.component.TextButton
import com.roko.kronos.processor.notification.NotificationProcessor.postNotification

// todo - ensure that the Work does not remain active when notificationsEnabled is false (and false is the desired value)

@Composable fun SettingsView(navController: NavController) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var notificationsEnabled by remember { mutableStateOf(globalPrefs.SETTING_NOTIFICATIONS_ENABLED) } // todo - if notifications are enabled, check and request permission (SDK 33+)

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(modifier = Modifier.weight(0.8f), text = "Receive an alert when the device clock is out of sync")
            Switch(
                modifier = Modifier.weight(0.2f),
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
        Spacer(modifier = Modifier.height(20.dp))
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
