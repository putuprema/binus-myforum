package xyz.purema.binusmyforum.data.repository

import android.content.Context
import android.util.Log
import androidx.room.withTransaction
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import xyz.purema.binusmyforum.data.local.MyForumDatabase
import xyz.purema.binusmyforum.data.local.dao.CourseClassDao
import xyz.purema.binusmyforum.data.local.dao.CourseClassScheduleDao
import xyz.purema.binusmyforum.data.local.dao.CourseDao
import xyz.purema.binusmyforum.data.local.dao.StudentDao
import xyz.purema.binusmyforum.data.local.mapper.StudentDbMapper
import xyz.purema.binusmyforum.data.local.model.CourseClassDb
import xyz.purema.binusmyforum.data.local.model.CourseClassScheduleDb
import xyz.purema.binusmyforum.data.local.model.CourseDb
import xyz.purema.binusmyforum.data.prefs.SharedPrefs
import xyz.purema.binusmyforum.data.remote.BinusmayaRemoteService
import xyz.purema.binusmyforum.data.remote.crypto.CryptoService
import xyz.purema.binusmyforum.data.remote.mapper.StudentRemoteMapper
import xyz.purema.binusmyforum.data.remote.model.request.BinusAuthRequest
import xyz.purema.binusmyforum.data.remote.model.response.courseschedule.BinusCourseClass
import xyz.purema.binusmyforum.data.worker.ReminderWorker
import xyz.purema.binusmyforum.domain.exception.AppException
import xyz.purema.binusmyforum.domain.model.ClassType
import xyz.purema.binusmyforum.domain.model.student.Student
import xyz.purema.binusmyforum.domain.repository.ForumRepository
import xyz.purema.binusmyforum.domain.repository.StudentRepository
import xyz.purema.binusmyforum.domain.utils.ApiUtils
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class StudentRepositoryImpl(
    private val context: Context,
    private val db: MyForumDatabase,
    private val forumRepository: ForumRepository,
    private val courseClassDao: CourseClassDao,
    private val courseClassScheduleDao: CourseClassScheduleDao,
    private val courseDao: CourseDao,
    private val studentDao: StudentDao,
    private val studentDbMapper: StudentDbMapper,
    private val binusmayaRemoteService: BinusmayaRemoteService,
    private val studentRemoteMapper: StudentRemoteMapper,
    private val cryptoService: CryptoService,
    private val apiUtils: ApiUtils,
    private val sharedPrefs: SharedPrefs
) : StudentRepository {
    private val TAG = this.javaClass.simpleName

    override suspend fun logout() {
        WorkManager.getInstance(context).cancelAllWork()

        withContext(Dispatchers.IO) {
            sharedPrefs.clear()
            db.clearAllTables()
        }
    }

    override suspend fun login(email: String, password: String): Student {
        try {
            val auth = binusmayaRemoteService.getAuthToken(
                BinusAuthRequest.GrantType.password,
                email = cryptoService.encryptParam(email),
                password = cryptoService.encryptParam(password)
            )

            sharedPrefs.lastEmail = email
            sharedPrefs.accessToken = auth.accessToken
            sharedPrefs.refreshToken = auth.refreshToken

            return getProfile()
        } catch (ex: Exception) {
            throw apiUtils.handleRequestError(ex)
        }
    }

    override suspend fun refreshToken(): Student {
        try {
            val refreshToken = sharedPrefs.refreshToken
            if (refreshToken != null) {
                val auth = binusmayaRemoteService.getAuthToken(
                    BinusAuthRequest.GrantType.refresh_token,
                    refreshToken = refreshToken
                )
                sharedPrefs.accessToken = auth.accessToken
                sharedPrefs.refreshToken = auth.refreshToken
                return getProfile()
            }
            throw AppException("Refresh token tidak tersedia", "")
        } catch (ex: Exception) {
            throw apiUtils.handleRequestError(ex)
        }
    }

    override suspend fun getProfile(): Student = try {
        val studentDb = studentDao.get()

        if (studentDb != null) {
            studentDbMapper.mapFromEntity(studentDb)
        } else {
            val studentRemote = binusmayaRemoteService.getStudentProfile()

            val student = studentRemoteMapper.mapFromEntity(studentRemote)
            studentDao.save(studentDbMapper.mapToEntity(student))

            student
        }
    } catch (ex: Exception) {
        throw apiUtils.handleRequestError(ex)
    }

    override suspend fun syncStudentData(student: Student) {
        if (sharedPrefs.courseDataSynchronized) {
            Log.d(TAG, "Student and course data already synchronized")
            return
        }

        Log.d(TAG, "Synchronizing student and course data with BINUSMAYA")

        db.withTransaction {
            // 1. Get student schedule terms list
            val terms = binusmayaRemoteService.getStudentTermList(student)
            student.strm = terms[terms.size - 1].strm

            // 2. Get course schedule
            val scheduleMap = hashMapOf<String, BinusCourseClass>()
            binusmayaRemoteService.getCourseSchedule(student)
                .forEach {
                    if (scheduleMap["${it.courseCode},${it.courseClass}"] != null) {
                        scheduleMap["${it.courseCode},${it.courseClass}"]?.eventCourseSchedule?.addAll(
                            it.eventCourseSchedule
                        )
                    } else {
                        scheduleMap["${it.courseCode},${it.courseClass}"] = it
                    }
                }

            // 3. Add enrolled courses and its associated class & schedule for the current student
            val dateFormat = DateTimeFormatter.ofPattern("dd MMM yyyy")

            val courseMap = hashMapOf<String, CourseDb>()
            val courseClassMap = hashMapOf<String, CourseClassDb>()
            val courseClassScheduleList = mutableSetOf<CourseClassScheduleDb>()

            binusmayaRemoteService.getStudentCourses(student)
                .forEach { enrolledCourse ->
                    val courseId = enrolledCourse.courseID
                    val courseCode = enrolledCourse.courseCode
                    val courseTitle = enrolledCourse.courseTitle
                    courseMap[courseId] = courseMap[courseId]
                        ?: CourseDb(courseId, courseCode, courseTitle)

                    val classNumber = enrolledCourse.classNumber
                    val classSection = enrolledCourse.classSection
                    val classType = enrolledCourse.ssrComponent

                    courseClassMap[classNumber] = courseClassMap[classNumber]
                        ?: CourseClassDb(
                            classNumber,
                            courseId,
                            classSection,
                            ClassType.valueOf(classType)
                        )

                    courseClassScheduleList.addAll(
                        scheduleMap["${courseCode},${classSection}"]!!.eventCourseSchedule
                            .map {
                                CourseClassScheduleDb(
                                    courseClassMap[classNumber]!!.id,
                                    it.eventType,
                                    LocalDate.parse(it.eventDate, dateFormat)
                                )
                            }
                    )
                }

            studentDao.update(studentDbMapper.mapToEntity(student))
            courseDao.save(*courseMap.values.toTypedArray())
            courseClassDao.save(*courseClassMap.values.toTypedArray())
            courseClassScheduleDao.save(*courseClassScheduleList.toTypedArray())
        }

        sharedPrefs.courseDataSynchronized = true
        Log.d(TAG, "Data synchronization completed")

        forumRepository.syncForumData()

        // schedule forum reply reminder once a day
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                ReminderWorker.TAG,
                ExistingPeriodicWorkPolicy.REPLACE,
                PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS).build()
            )
    }
}