package com.roko.kronos.processor.background

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.roko.kronos.MainApplication.Companion.applicationContext
import com.roko.kronos.model.RepeatInterval
import com.roko.kronos.util.Logger.log

object WorkManagerHandler {

    // todo - see if the work manager can be cancelled more ok: https://developer.android.com/guide/background/persistent/how-to/manage-work#stop-worker

    private val workManager by lazy { WorkManager.getInstance(applicationContext()) }

    fun setWorkManager(repeatInterval: RepeatInterval) {
        log("CP-TO_CHECKED")
        cancelWorkManager(withLog = false)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
        val periodicWorkRequest = PeriodicWorkRequestBuilder<TimeCheckBackgroundWork>(repeatInterval.amount, repeatInterval.timeUnit)
            .setConstraints(constraints)
            .build()
        workManager.enqueue(periodicWorkRequest)
    }

    fun cancelWorkManager(withLog: Boolean = true) {
        if (withLog) log("CP-TO_UNCHECKED")
        workManager.cancelAllWork()
    }

}