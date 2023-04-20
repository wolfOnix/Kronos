package com.roko.kronos

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.roko.kronos.util.State
import com.roko.kronos.processor.TimeProcessor

class MainActivity : ComponentActivity() {

    private val isAutoTimeEnabled: () -> Boolean = { Settings.Global.getInt(contentResolver, Settings.Global.AUTO_TIME) == 1 }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        State.setIsAutoTimeEnabled(enabled = isAutoTimeEnabled())
        /*try {
            TimeProcessor.setNetworkTime()
        } catch (_: NoNetworkExc) {
            State.raiseNoNetworkConnection()
        }*/

        // ?? if (global.NOTIFICATIONS_ON && workManagerNotAlreadySet /* when app is first used and the user granted notification permissions */) WorkManagerHandler.setWorkManager(repeatInterval = global.CHOSEN_REPEAT_INTERVAL)

        setContent {
            KronosApp(
                context = applicationContext,
                toClockSettingsAction = { startActivity(Intent(Settings.ACTION_DATE_SETTINGS)) }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        State.setIsAutoTimeEnabled(enabled = isAutoTimeEnabled())
        TimeProcessor.updateTime()
    }

    // onTick -> updateTime()

}
