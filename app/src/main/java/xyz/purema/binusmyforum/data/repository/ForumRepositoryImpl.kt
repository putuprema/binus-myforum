package xyz.purema.binusmyforum.data.repository

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.apache.commons.lang3.StringUtils
import xyz.purema.binusmyforum.R
import xyz.purema.binusmyforum.data.local.dao.*
import xyz.purema.binusmyforum.data.local.mapper.ForumThreadDbMapper
import xyz.purema.binusmyforum.data.local.mapper.StudentDbMapper
import xyz.purema.binusmyforum.data.remote.BinusmayaRemoteService
import xyz.purema.binusmyforum.data.worker.ForumDataSyncWorker
import xyz.purema.binusmyforum.domain.exception.AppException
import xyz.purema.binusmyforum.domain.model.forum.ForumThread
import xyz.purema.binusmyforum.domain.model.forum.GslcForum
import xyz.purema.binusmyforum.domain.model.forum.GslcForumQueryResult
import xyz.purema.binusmyforum.domain.repository.ForumRepository
import xyz.purema.binusmyforum.domain.utils.ApiUtils
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
class ForumRepositoryImpl(
    private val context: Context,
    private val studentDao: StudentDao,
    private val courseDao: CourseDao,
    private val courseClassDao: CourseClassDao,
    private val classScheduleDao: CourseClassScheduleDao,
    private val forumThreadDao: ForumThreadDao,
    private val forumThreadDbMapper: ForumThreadDbMapper,
    private val binusmayaRemoteService: BinusmayaRemoteService,
    private val studentDbMapper: StudentDbMapper,
    private val apiUtils: ApiUtils
) : ForumRepository {
    override suspend fun getGslcForum(includeReplied: Boolean): GslcForumQueryResult {
        val result = mutableListOf<GslcForum>()

        var unreplied = 0L

        val gslcSchedules = classScheduleDao.getGslcSchedules()
        for (gslcSchedule in gslcSchedules) {
            val courseClass = courseClassDao.getById(gslcSchedule.courseClassId) ?: continue
            val course = courseDao.getById(courseClass.courseId) ?: continue

            val forumThreads = forumThreadDao.getForumThreads(
                courseClass.id,
                gslcSchedule.date.toString(),
                gslcSchedule.date.plusDays(7).toString()
            )

            val forum = GslcForum(
                classCode = courseClass.classSection,
                classType = courseClass.classType.description,
                course = course.courseTitle,
                startDate = gslcSchedule.date,
                dueDate = gslcSchedule.date.plusDays(7),
                forumThread = if (forumThreads.isNotEmpty())
                    forumThreadDbMapper.mapFromEntity(forumThreads[0]) else null
            )

            if (forum.forumThread?.replyMessage == null) unreplied += 1
            else if (!includeReplied) continue

            result.add(forum)
        }

        return GslcForumQueryResult(unreplied = unreplied, list = result)
    }

    override suspend fun syncForumData() {
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                ForumDataSyncWorker.TAG,
                ExistingPeriodicWorkPolicy.REPLACE,
                PeriodicWorkRequestBuilder<ForumDataSyncWorker>(8, TimeUnit.HOURS)
                    .setConstraints(
                        Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                    )
                    .build()
            )
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun replyForum(forum: ForumThread, attachmentUri: Uri?) =
        withContext(Dispatchers.IO) {
            val studentDb = studentDao.get()
            if (studentDb != null) {
                if (StringUtils.isEmpty(forum.replyMessage)) {
                    throw AppException(context.getString(R.string.empty_reply_message), "")
                }

                val student = studentDbMapper.mapFromEntity(studentDb)

                var attachment: MultipartBody.Part? = null

                if (attachmentUri != null) {
                    val cursor =
                        context.contentResolver.query(attachmentUri, null, null, null, null)

                    if (cursor != null && cursor.moveToFirst()) {
                        val fileName =
                            cursor.getString(cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME))
                        val mediaType =
                            cursor.getString(cursor.getColumnIndex(DocumentsContract.Document.COLUMN_MIME_TYPE))

                        val inputStream = context.contentResolver.openInputStream(attachmentUri)
                        if (inputStream != null) {
                            attachment = MultipartBody.Part.createFormData(
                                "attachment", fileName, RequestBody.create(
                                    MediaType.get(mediaType),
                                    inputStream.readBytes()
                                )
                            )
                        }

                        inputStream?.close()
                    }

                    cursor?.close()
                }

                try {
                    binusmayaRemoteService.replyForum(student, forum, attachment)
                } catch (ex: Exception) {
                    throw apiUtils.handleRequestError(ex)
                }

                forumThreadDao.update(forumThreadDbMapper.mapToEntity(forum))
            }
        }
}