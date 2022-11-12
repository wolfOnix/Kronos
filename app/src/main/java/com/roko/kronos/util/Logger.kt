package com.roko.kronos.util

object Logger {

    fun log(message: String) {
        println("CP - $message")
    }

    fun logError(e: Exception) {
        println("ERR - ${e.message}")
    }

}
