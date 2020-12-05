package xyz.purema.binusmyforum

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class BinusMyForumApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(
                id = NOTIFICATION_CHANNEL_ID_MAIN,
                name = "Main",
                description = "Main notification channel",
                importance = NotificationManager.IMPORTANCE_HIGH
            )
            createNotificationChannel(
                id = NOTIFICATION_CHANNEL_ID_SERVICES,
                name = "Services",
                description = "Notification channel for background services",
                importance = NotificationManager.IMPORTANCE_DEFAULT
            )
        }
    }

    companion object {
        const val UPLOAD_FILE_SIZE_LIMIT_MB = 10
        const val NOTIFICATION_CHANNEL_ID_MAIN = "main"
        const val NOTIFICATION_CHANNEL_ID_SERVICES = "services"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(
        id: String,
        name: String,
        description: String,
        importance: Int
    ) {
        val channel = NotificationChannel(id, name, importance).apply {
            this.description = description
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }
}