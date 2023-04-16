package com.roko.kronos.processor.background

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.roko.kronos.model.NotificationData
import com.roko.kronos.processor.notification.NotificationProcessor

class TimeCheckBackgroundWork(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {

    override fun doWork(): Result {
        NotificationProcessor.postNotification(NotificationData(title = "Out of sync", preview = "Automatic notification", content = "This is an automatic notification. Your device is well behind the real clock - 4 years, 34 minutes and 53 milliseconds"))
        return Result.success()
    }

}
