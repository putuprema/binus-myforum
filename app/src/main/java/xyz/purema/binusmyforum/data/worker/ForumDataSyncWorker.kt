package xyz.purema.binusmyforum.data.worker

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.room.withTransaction
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import kotlinx.coroutines.ExperimentalCoroutinesApi
import xyz.purema.binusmyforum.BinusMyForumApplication
import xyz.purema.binusmyforum.R
import xyz.purema.binusmyforum.data.local.MyForumDatabase
import xyz.purema.binusmyforum.data.local.dao.CourseClassDao
import xyz.purema.binusmyforum.data.local.dao.ForumThreadDao
import xyz.purema.binusmyforum.data.local.dao.StudentDao
import xyz.purema.binusmyforum.data.local.mapper.CourseClassDbMapper
import xyz.purema.binusmyforum.data.local.mapper.ForumThreadDbMapper
import xyz.purema.binusmyforum.data.local.mapper.StudentDbMapper
import xyz.purema.binusmyforum.data.remote.BinusmayaRemoteService
import xyz.purema.binusmyforum.data.remote.mapper.ForumThreadRemoteMapper
import xyz.purema.binusmyforum.domain.repository.StudentRepository
import xyz.purema.binusmyforum.domain.utils.NotificationUtils
import xyz.purema.binusmyforum.ui.activity.SplashActivity

class ForumDataSyncWorker @WorkerInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val studentRepository: StudentRepository,
    private val studentDao: StudentDao,
    private val studentDbMapper: StudentDbMapper,
    private val db: MyForumDatabase,
    private val courseClassDao: CourseClassDao,
    private val courseClassDbMapper: CourseClassDbMapper,
    private val binusmayaRemoteService: BinusmayaRemoteService,
    private val forumThreadRemoteMapper: ForumThreadRemoteMapper,
    private val forumThreadDbMapper: ForumThreadDbMapper,
    private val forumThreadDao: ForumThreadDao
) : CoroutineWorker(context, params) {
    companion object {
        const val TAG = "ForumDataSyncWorker"
    }

    private var countFailed = 0

    val notificationTitle = "Mengupdate daftar forum dari BINUSMAYA"
    private val notification = NotificationCompat.Builder(
        applicationContext,
        BinusMyForumApplication.NOTIFICATION_CHANNEL_ID_SERVICES
    )
        .setOnlyAlertOnce(true)
        .setContentTitle(notificationTitle)
        .setTicker(notificationTitle)
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_clock)

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun doWork(): Result {
        setForeground(createForegroundInfo(indeterminate = true))
        syncForumData()

        val stringBuilder = StringBuilder("Daftar forum berhasil diupdate.")
        if (countFailed > 0) {
            stringBuilder.append(" ($countFailed gagal)")
        }
        NotificationUtils.createNotification(
            applicationContext,
            channelId = BinusMyForumApplication.NOTIFICATION_CHANNEL_ID_SERVICES,
            title = "Sinkronisasi Berhasil",
            message = stringBuilder.toString(),
            activityToOpen = SplashActivity::class.java
        )
        return Result.success()
    }

    private suspend fun syncForumData() {
        val studentDb = studentDao.get() ?: return
        Log.i(TAG, "Synchronizing forum data with BINUSMAYA...")
        val student = studentDbMapper.mapFromEntity(studentDb)
        var progress = 0

        studentRepository.refreshToken()

        db.withTransaction {
            val courseClasses = courseClassDao.getAll()
            setForeground(createForegroundInfo())
            val progressIncrement = 100 / courseClasses.size

            for (courseClassDb in courseClasses) {
                val courseClass = courseClassDbMapper.mapFromEntity(courseClassDb)
                try {
                    val forumThreads = binusmayaRemoteService.getForumThreads(student, courseClass)
                    for (bForumThread in forumThreads) {
                        val ft = forumThreadRemoteMapper.mapFromEntity(bForumThread)
                        ft.courseClassId = courseClass.id

                        val posts = binusmayaRemoteService.getForumThreadPosts(student, ft)
                        if (posts.isNotEmpty()) {
                            ft.creatorMessage = posts[0].message
                            ft.firstPostId = posts[0].postId
                            ft.attachmentLink = posts[0].uploadPath

                            val reply = posts.find { p -> p.creatorId == student.binusianId }
                            if (reply != null) {
                                ft.replyMessage = reply.message
                            } else {
                                ft.replyMessage = null
                            }
                        }

                        val forumThreadDb = forumThreadDbMapper.mapToEntity(ft)
                        forumThreadDao.save(forumThreadDb)
                    }
                } catch (e: Exception) {
                    Log.e(
                        TAG,
                        String.format(
                            "Exception caught when getting forum threads for class [%s]: %s",
                            courseClass.classSection,
                            e.message
                        ),
                        e
                    )
                    countFailed++
                }

                progress += progressIncrement
                setForeground(createForegroundInfo(progress))
            }
        }

        Log.i(TAG, "Forum data synchronization completed")
    }

    private fun createForegroundInfo(
        progress: Int = 0,
        indeterminate: Boolean = false
    ): ForegroundInfo {
        notification.setProgress(100, progress, indeterminate)
        return ForegroundInfo(1, notification.build())
    }
}