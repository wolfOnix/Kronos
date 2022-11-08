package com.roko.kronos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.format.DateFormat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.roko.kronos.ui.component.CentredText
import com.roko.kronos.ui.component.Clock
import com.roko.kronos.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.net.ntp.NTPUDPClient
import org.apache.commons.net.ntp.TimeInfo
import java.net.InetAddress
import java.util.*
import kotlin.math.absoluteValue

class MainActivity : ComponentActivity() {

    private val autoTimeSetting = { Settings.Global.getInt(contentResolver, Settings.Global.AUTO_TIME) == 1 }
    private var isAutoTime by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isAutoTime = autoTimeSetting()
        var realTime: Long? by mutableStateOf(null)

        setContent {
            val coroutineScope = rememberCoroutineScope()
            /* LaunchedEffect(Unit) {
                while (true) {
                    // update times (only deviceTime if realTime is null)
                    delay(60000L) // first time less
                }
            } */
            LaunchedEffect(Unit) {
                coroutineScope.launch {
                    realTime = getNetworkTime()
                    println("CP-IM1 - $realTime")
                }
            }

            KronosTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                            .padding(15.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CentredText(text = stringResource(id = R.string.hello))
                        CentredText(
                            text = stringResource(id = R.string.auto_time_set_is, stringResource(id = if (isAutoTime) R.string.enabled else R.string.disabled)),
                            colour = if (isAutoTime) MaterialTheme.colorScheme.onBackground else Red
                        )
                        realTime.let {
                            val deviceTime = System.currentTimeMillis()
                            Row(modifier = Modifier.padding(20.dp)) {
                                Clock(titleRes = R.string.device_clock, time = deviceTime.toTimeString(context = applicationContext))
                                Clock(titleRes = R.string.real_clock, time = it?.toTimeString(context = applicationContext) ?: "--:--")
                            }
                            if (it != null) {
                                val timeDifference = (it - deviceTime) / 1000L
                                (timeDifference.absoluteValue to timeDifference.toDifferenceString()).let { (absDiffAsSec, diffString) ->
                                    if (absDiffAsSec >= 10L && diffString != null) {
                                        CentredText(
                                            text = stringResource(
                                                id = R.string.your_device_clock_is_,
                                                diffString,
                                                stringResource(id = if (timeDifference >= 10L) R.string.behind else R.string.ahead
                                                )),
                                            colour = when (absDiffAsSec) {
                                                in 10L until 30L -> Yellow
                                                in 30L until 60L -> Orange
                                                in 60L until 1200L -> DarkOrange
                                                else -> Red
                                            }
                                        )
                                    } else {
                                        CentredText(
                                            text = stringResource(id = R.string.your_device_clock_is_synchronised_),
                                            colour = Green
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }
                        CentredText(text = stringResource(id = R.string.to_synchronise_the_clock_,
                            stringResource(id = if (isAutoTime) R.string.disable_and_re_enable else R.string.enable)))
                        CentredText(text = "* ${stringResource(id = R.string.the_option_could_be_named_differently)}", colour = Gray, fontSizeInt = 13)
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(onClick = { startActivity(Intent(Settings.ACTION_DATE_SETTINGS)) }) {
                            Text(text = stringResource(id = R.string.open_time_settings))
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isAutoTime = autoTimeSetting()
    }

    private suspend fun getNetworkTime(): Long? = withContext(Dispatchers.IO) {
        println("CP-NTC")
        try {
            val timeClient = NTPUDPClient()
            val inetAddress: InetAddress = InetAddress.getByName("time-a.nist.gov")
            inetAddress.isReachable(5000)
            val timeInfo: TimeInfo = timeClient.getTime(inetAddress)
            timeClient.close()
            timeInfo.message.receiveTimeStamp.time
        } catch (e: Exception) {
            println("CP-RG1 - ${e.message}")
            null
        }
    }

    private fun Long.toTimeString(context: Context): String {
        val dateFormat = DateFormat.getTimeFormat(context)
        return dateFormat.format(Date(this))
    }

    private fun Long.toDifferenceString(): String? = when (val absAsSec = absoluteValue / 1000L) {
        0L -> null
        in 1L until 60L -> "$absAsSec sec"
        in 60L until 3600L -> {
            val m = absAsSec / 60L
            "$m min ${absAsSec - m * 60L} sec"
        }
        else -> {
            val h = absAsSec / 3600L
            val m = (absAsSec - h * 3600L) / 60L
            "$h h $m min ${absAsSec - h * 3600L - m * 60L} sec"
        }
    }

}
