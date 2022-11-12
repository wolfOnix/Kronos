package com.roko.kronos.util

import android.content.Context
import android.text.format.DateFormat
import java.util.*
import kotlin.math.absoluteValue

fun Long.toTimeString(context: Context): String {
    val dateFormat = DateFormat.getTimeFormat(context)
    return dateFormat.format(Date(this))
}

/** This number must be expressed in seconds. Can be expressed as negative number, too, returning the same result. */
fun Long.toDifferenceString(): String? = when (val absAsSec = absoluteValue) {
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
