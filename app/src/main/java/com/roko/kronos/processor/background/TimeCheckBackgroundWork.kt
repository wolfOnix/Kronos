package com.roko.kronos.processor.background

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.roko.kronos.model.NotificationData
import com.roko.kronos.processor.notification.NotificationProcessor

class TimeCheckBackgroundWork(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {

    override fun doWork(): Result {
        NotificationProcessor.postNotification(NotificationData(title = "Automatic notification", preview = "Some time has passed...", content = "Some time has passed since you have last verified if your clock is in time - pun intended. Open the app and verify if everything is all right!"))
        return Result.success()
    }

}
