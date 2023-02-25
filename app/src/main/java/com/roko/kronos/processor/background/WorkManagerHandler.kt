package com.roko.kronos.processor.background

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.roko.kronos.MainApplication.Companion.applicationContext
import com.roko.kronos.model.RepeatInterval

object WorkManagerHandler {

    private val workManager by lazy { WorkManager.getInstance(applicationContext()) }

    fun setWorkManager(repeatInterval: RepeatInterval) {
        cancelWorkManager()
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
        val periodicWorkRequest = PeriodicWorkRequestBuilder<TimeCheckBackgroundWork>(repeatInterval.amount, repeatInterval.timeUnit)
            .setConstraints(constraints)
            .build()
        workManager.enqueue(periodicWorkRequest)
    }

    fun cancelWorkManager() {
        workManager.cancelAllWork()
    }

}