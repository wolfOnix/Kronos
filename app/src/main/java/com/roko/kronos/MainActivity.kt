package com.roko.kronos

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.roko.kronos.exceptions.NoNetworkExc
import com.roko.kronos.util.State
import com.roko.kronos.processors.TimeProcessor

class MainActivity : ComponentActivity() {

    private val isAutoTimeEnabled = { Settings.Global.getInt(contentResolver, Settings.Global.AUTO_TIME) == 1 }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        State.setIsAutoTimeEnabled(enabled = isAutoTimeEnabled())
        try {
            TimeProcessor.setNetworkTime()
        } catch (_: NoNetworkExc) {
            State.raiseNoNetworkConnection()
        }

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
