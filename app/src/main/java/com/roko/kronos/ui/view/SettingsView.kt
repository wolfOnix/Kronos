package com.roko.kronos.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.roko.kronos.R
import com.roko.kronos.ui.component.TextButton
import com.roko.kronos.processors.notification.NotificationProcessor.postNotification

@Composable fun SettingsView(navController: NavController) {
    Column {
        var notificationsEnabled by remember { mutableStateOf(false) } // todo - if notifications are enabled, check and request permission (SDK 33+)

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(modifier = Modifier.weight(0.8f), text = "Receive an alert when the device clock is out of sync")
            Switch(modifier = Modifier.weight(0.2f), checked = notificationsEnabled, onCheckedChange = { notificationsEnabled = !notificationsEnabled })
        }

        if (notificationsEnabled) {
            TextButton(
                stringRes = R.string.out_of_sync_alerts,
                onClick = { postNotification() }
            )
        }
    }
}
