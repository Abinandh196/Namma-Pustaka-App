package com.example.nammapustaka

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.nammapustaka.workers.OverdueCheckWorker
import java.util.concurrent.TimeUnit

class NammaPustakaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        val workRequest = PeriodicWorkRequestBuilder<OverdueCheckWorker>(1, TimeUnit.DAYS)
            .build()
            
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "OverdueCheck",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
