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

    /*fun setNetworkTime() = runBlocking { // todo - remove runBlocking as it blocks the thread; removing this seems to make "Davey" warning no longer appear (Hurray!)
        launch {
            getNetworkTime()?.let {
                networkTimeMillis = it
                val currentMillis = System.currentTimeMillis()
                deviceTimeMillis = currentMillis
                deltaMillis = it - currentMillis
            }
        }
    }*/

    fun updateTime() {
        log("UPD-TIME")
        val currentMillis = System.currentTimeMillis()
        deviceTimeMillis = currentMillis
        if (networkTimeMillis != null) deltaMillis?.let { delta ->
            networkTimeMillis = currentMillis + delta
        }
    }

    suspend fun getNetworkTime(): Long? = withContext(Dispatchers.IO) {
        log("GET-NET-TIME")
        return@withContext try {
            val inetAddress = InetAddress.getByName("0.pool.ntp.org").apply {
                isReachable(5000)
            }
            val timeClient = NTPUDPClient()
            val timeInfo: TimeInfo = timeClient.getTime(inetAddress)
            timeClient.close()
            timeInfo.message.receiveTimeStamp.time.also { log("Received NETZ timestamp $it") }
        } catch (e: Exception) {
            logError(e)
            when (e) {
                is UnknownHostException, is SocketTimeoutException -> throw NoNetworkExc()
                else -> null
            }
        }
    }

}
