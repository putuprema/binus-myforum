package xyz.purema.binusmyforum.data.worker

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.ExperimentalCoroutinesApi
import xyz.purema.binusmyforum.domain.repository.ForumRepository
import xyz.purema.binusmyforum.domain.utils.NotificationUtils
import xyz.purema.binusmyforum.ui.activity.SplashActivity
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ReminderWorker @WorkerInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val forumRepository: ForumRepository
) : CoroutineWorker(context, params) {
    companion object {
        const val TAG = "ReminderWorker"
    }

    @ExperimentalCoroutinesApi
    override suspend fun doWork(): Result {
        val result = forumRepository.getGslcForum(includeReplied = false)
        result.list.forEach {
            val remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), it.dueDate)
            if (it.forumThread != null) {
                NotificationUtils.createNotification(
                    applicationContext,
                    title = "Ada forum yang belum dibalas",
                    message = "Forum ${it.forumThread!!.subject} $remainingDays hari lagi. Jangan lupa dibalas hey !!!",
                    priority = NotificationCompat.PRIORITY_HIGH,
                    activityToOpen = SplashActivity::class.java
                )
            }
        }
        return Result.success()
    }
}