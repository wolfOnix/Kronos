package com.roko.kronos

import android.app.Application
import android.content.Context

class MainApplication : Application() {

    companion object {

        lateinit var instance: MainApplication
            private set

        /** The current Context instance of the application. */
        fun applicationContext(): Context = instance.applicationContext

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

}
