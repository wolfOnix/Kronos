package com.roko.kronos.processor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.roko.kronos.exception.NoNetworkExc
import com.roko.kronos.util.Logger.log
import com.roko.kronos.util.Logger.logError
import kotlinx.coroutines.*
import org.apache.commons.net.ntp.NTPUDPClient
import org.apache.commons.net.ntp.TimeInfo
import java.net.InetAddress
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object TimeProcessor {

    var deviceTimeMillis: Long by mutableStateOf(System.currentTimeMillis())
        private set
    var networkTimeMillis: Long? by mutableStateOf(null)
        private set
    private var deltaMillis: Long? = null

    fun setNetworkTime() = runBlocking {
        launch {
            withContext(Dispatchers.IO) {
                log("SET-NET-TIME")
                try {
                    val inetAddress = InetAddress.getByName("0.pool.ntp.org").apply {
                        isReachable(5000)
                    }
                    val timeClient = NTPUDPClient()
                    val timeInfo: TimeInfo = timeClient.getTime(inetAddress)
                    timeClient.close()
                    timeInfo.message.receiveTimeStamp.time.let {
                        networkTimeMillis = it
                        val currentMillis = System.currentTimeMillis()
                        deviceTimeMillis = currentMillis
                        deltaMillis = it - currentMillis
                    }
                } catch (e: Exception) {
                    logError(e)
                    when (e) {
                        is UnknownHostException, is SocketTimeoutException -> throw NoNetworkExc()
                    }
                }
            }
        }
    }

    fun updateTime() {
        log("UPD-TIME")
        val currentMillis = System.currentTimeMillis()
        deviceTimeMillis = currentMillis
        if (networkTimeMillis != null) deltaMillis?.let { delta ->
            networkTimeMillis = currentMillis + delta
        }
    }

}
