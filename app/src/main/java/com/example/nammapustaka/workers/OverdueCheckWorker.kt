package com.example.nammapustaka.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.nammapustaka.data.AppDatabase
import kotlinx.coroutines.flow.first

class OverdueCheckWorker(
    private val ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        return try {
            val database = AppDatabase.getDatabase(ctx)
            val libraryDao = database.libraryDao()

            val currentTime = System.currentTimeMillis()
            val overdueTransactions = libraryDao.getOverdueTransactions(currentTime).first()

            if (overdueTransactions.isNotEmpty()) {
                showNotification(overdueTransactions.size)
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun showNotification(count: Int) {
        val notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "overdue_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Overdue Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(ctx, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Overdue Books")
            .setContentText("There are $count overdue books. Please remind the students.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification)
    }
}
