package xyz.purema.binusmyforum.data.service

import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import xyz.purema.binusmyforum.BinusMyForumApplication
import xyz.purema.binusmyforum.domain.utils.NotificationUtils

class MessagingService : FirebaseMessagingService() {

    @ExperimentalCoroutinesApi
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(MessagingService::class.java.simpleName, "Got message from: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.size > 0) {
            Log.d(
                MessagingService::class.java.simpleName,
                "Message data payload: ${remoteMessage.data}"
            )
        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d(
                MessagingService::class.java.simpleName,
                "Message Notification Body: ${remoteMessage.notification!!.body}"
            )

            NotificationUtils.createNotification(
                ctx = this,
                channelId = BinusMyForumApplication.NOTIFICATION_CHANNEL_ID_MAIN,
                title = remoteMessage.notification!!.title ?: "Notification",
                message = remoteMessage.notification!!.body,
                priority = NotificationCompat.PRIORITY_HIGH
            )
        }
    }

    override fun onNewToken(token: String) {
        Log.d(MessagingService::class.java.simpleName, "Got new FCM token: $token")
    }
}