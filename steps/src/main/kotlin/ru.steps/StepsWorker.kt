package ru.steps

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class StepCounterWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    private var notificationManager: NotificationManager? = null
    private val counter by lazy { StepCounter(context) }

    override suspend fun doWork(): Result {
        createNotificationChannel()
        setForeground(createForegroundInfo())
        counter.startCounting()
        while (true) {
            delay(10_000)
            updateNotification(counter.totalSteps)
        }
        return Result.success()
    }

    private fun createForegroundInfo(): ForegroundInfo {
        val notification = createNotification(0)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ForegroundInfo(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
            )
        } else {
            ForegroundInfo(NOTIFICATION_ID, notification)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Cчитаем шаги",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows current step count"
            }

            notificationManager = applicationContext
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun createNotification(progress: Int): Notification {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Считаем шаги")
            .setTicker("Steps пашка")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentText(progress.toString())
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(steps: Int) {
        notificationManager?.notify(NOTIFICATION_ID, createNotification(steps))
    }

    companion object {
        const val CHANNEL_ID = "step_counter_channel"
        const val NOTIFICATION_ID = 1
        const val WORK_TAG = "step_counter_work"
    }
}