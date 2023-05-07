@file:Suppress("PropertyName")

package com.roko.kronos.util

import android.content.Context
import com.roko.kronos.constants.DEFAULT_NOTIFICATIONS_ENABLED
import com.roko.kronos.util.GlobalPreferences.PreferenceKeys.*
import java.lang.ClassCastException

class GlobalPreferences(context: Context) {

    private val preferences = context.getSharedPreferences("kronos_globals", Context.MODE_PRIVATE) ?: error("Getting shared preferences from context failed")

    private enum class PreferenceKeys {
        NOTIFICATIONS_ENABLED
    }

    var SETTING_NOTIFICATIONS_ENABLED: Boolean
        get() = try {
            preferences.getBoolean(NOTIFICATIONS_ENABLED.name, DEFAULT_NOTIFICATIONS_ENABLED)
        } catch (_: ClassCastException) {
            DEFAULT_NOTIFICATIONS_ENABLED
        }
        set(value) = preferences.edit().putBoolean(NOTIFICATIONS_ENABLED.name, value).apply()

}
