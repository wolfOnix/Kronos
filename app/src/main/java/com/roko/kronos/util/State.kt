package com.roko.kronos.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object State {

    var isAutoTimeEnabled by mutableStateOf(false)
        private set
    var isNetworkConnection by mutableStateOf(true)
        private set

    fun setIsAutoTimeEnabled(enabled: Boolean) {
        isAutoTimeEnabled = enabled
    }

    fun raiseNoNetworkConnection() {
        isNetworkConnection = false
    }

}

enum class ClockState {
    /** The network time has not yet been discovered but has been requested. */
    PENDING,
    /** There is no internet connection and the network time could not be requested. */
    NO_INTERNET,
    /** The network time has been requested and is known. */
    KNOWN,
}
