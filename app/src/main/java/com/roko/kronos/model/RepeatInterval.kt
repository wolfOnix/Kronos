package com.roko.kronos.model

import java.util.concurrent.TimeUnit

enum class RepeatInterval(val amount: Long, val timeUnit: TimeUnit) {
    ONE_DAY(1L, TimeUnit.DAYS),
    FIFTEEN_MINUTES(15, TimeUnit.MINUTES), // the minimum practically allowed interval
}
