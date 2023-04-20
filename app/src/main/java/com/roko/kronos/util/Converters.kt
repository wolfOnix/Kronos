@file:JvmName("Converters")

package com.roko.kronos.util

import android.content.Context
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.roundToLong

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.S")
private val zoneId = ZoneId.systemDefault()

// todo - find better solution for localised pattern that includes seconds and uses one-time initialised formatter (unlike initialising it on every function call)
// todo - make context globally reachable (if needed)
fun Long.asTimeString(context: Context): String {
    val instant = Instant.ofEpochMilli(this)
    return timeFormatter.format(instant.atZone(zoneId))
}

/*
fun Long.asTimeString(context: Context): String {
    val dateFormat = android.text.format.DateFormat.getTimeFormat(context)
    return dateFormat.format(Date(this))
}
*/

/** This number must be expressed in seconds. Can be expressed as negative number, too, returning the same result. */
/*
    todo - see https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/ - toComponents()
        duration.toComponents { minutes, seconds, _ -> String.format("%02d:%02d", minutes, seconds) } - days and hours can be used too
*/
fun Long.asDifferenceString(): String? = when (val absAsSec = absoluteValue) {
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

/**
 * Takes **this** Pair of _deviceTimeMillis_ to _networkTimeMillis_ and harmonises the network time in correspondence to the device time which is considered the reference, so that both times
 * have a synchronous tick on UI refreshes.
 * @param precisionDrop Represents the number of least significant digit positions that should be dropped to zero, while rounding the resulting numbers as needed. The precision drop should be the
 * highest value lower than the orders that are visible in the UI (i.e., if the UI converts a long value to the string format _HH:mm:ss.S_ it means that the least two digits need to be dropped and
 * the rest preserved - the hundredth and the thousandth of a second can be dropped as they are not expressed in the UI).
 * @return A Pair of _harmonisedNetworkTimeMillis_ to _harmonisedDeltaMillis_ (positive if deviceTimeMillis <=).
 */
fun Pair<Long, Long>.toHarmonisedNetworkTimeAndDeltaMillis(precisionDrop: Int = 2): Pair<Long, Long> {
    val deviceTimeMillis = this.first // d
    val networkTimeMillis = this.second // n
    if (deviceTimeMillis == networkTimeMillis) return this.second to 0L

    val deltaMillis = networkTimeMillis - deviceTimeMillis // Δ
    if (precisionDrop == 0) return this.second to deltaMillis

    val harmoniser = 10f.pow(precisionDrop.absoluteValue)
    val harmonisedDeltaMillis = (deltaMillis / harmoniser).roundToLong() * harmoniser.toLong() // ~Δ
    val harmonisedNetworkTimeMillis = deviceTimeMillis + harmonisedDeltaMillis // ñ
    return harmonisedNetworkTimeMillis to harmonisedDeltaMillis
}
