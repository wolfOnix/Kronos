package com.roko.kronos

import android.app.Application
import android.content.Context
import com.roko.kronos.util.GlobalPreferences

val globalPrefs: GlobalPreferences by lazy {
    MainApplication.globalPreferences ?: error("Global preferences initialisation failed")
}

class MainApplication : Application() {

    companion object {

        lateinit var instance: MainApplication
            private set

        var globalPreferences: GlobalPreferences? = null

        /** The current Context instance of the application. */
        fun applicationContext(): Context = instance.applicationContext

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        globalPreferences = GlobalPreferences(this.applicationContext)
    }

}
