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
