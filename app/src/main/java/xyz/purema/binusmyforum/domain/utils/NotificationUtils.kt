package xyz.purema.binusmyforum.domain.utils

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import xyz.purema.binusmyforum.BinusMyForumApplication
import xyz.purema.binusmyforum.R
import kotlin.random.Random

@ExperimentalCoroutinesApi
object NotificationUtils {
    fun createNotification(
        ctx: Context,
        channelId: String = BinusMyForumApplication.NOTIFICATION_CHANNEL_ID_MAIN,
        title: String,
        message: String?,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT,
        activityToOpen: Class<out Activity>? = null
    ) {
        val pendingIntent = if (activityToOpen != null) {
            val intent = Intent(ctx, activityToOpen).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            PendingIntent.getActivity(ctx, 0, intent, 0)
        } else null

        val notification = NotificationCompat.Builder(ctx, channelId)
            .setSmallIcon(R.drawable.ic_clock)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(priority)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(ctx)) {
            notify(Random.nextInt(), notification)
        }
    }
}