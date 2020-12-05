package xyz.purema.binusmyforum.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import xyz.purema.binusmyforum.data.prefs.SharedPrefs
import xyz.purema.binusmyforum.data.remote.crypto.CryptoService
import xyz.purema.binusmyforum.data.remote.model.request.*
import xyz.purema.binusmyforum.data.remote.model.response.BinusLoginResponse
import xyz.purema.binusmyforum.data.remote.model.response.BinusResponseEnvelope
import xyz.purema.binusmyforum.data.remote.model.response.BinusStudentProfileResponse
import xyz.purema.binusmyforum.data.remote.model.response.courseschedule.BinusCourseClass
import xyz.purema.binusmyforum.data.remote.model.response.forumthread.BinusForumThread
import xyz.purema.binusmyforum.data.remote.model.response.forumthreadpost.BinusForumThreadPost
import xyz.purema.binusmyforum.data.remote.model.response.studentcourse.BinusStudentCourse
import xyz.purema.binusmyforum.data.remote.model.response.studentterm.BinusStudentTerm
import xyz.purema.binusmyforum.domain.exception.AppException
import xyz.purema.binusmyforum.domain.model.course.CourseClass
import xyz.purema.binusmyforum.domain.model.forum.ForumThread
import xyz.purema.binusmyforum.domain.model.student.Student

class BinusmayaRemoteService(
    private val api: BinusmayaApiClient,
    private val cryptoService: CryptoService,
    private val sharedPrefs: SharedPrefs
) {
    private fun validateResponseStatus(res: BinusResponseEnvelope<*>) {
        if (res.code != "200") throw AppException(res.message ?: "Unknown error", res.code ?: "500")
    }

    suspend fun getAuthToken(
        grantType: BinusAuthRequest.GrantType,
        email: String? = null,
        password: String? = null,
        refreshToken: String? = null
    ): BinusLoginResponse {
        return api.getAuthToken(grantType, email, password, refreshToken)
    }

    suspend fun getStudentProfile(): BinusStudentProfileResponse {
        // 1. Get binusian ID from bimay if not exist yet
        var binusianId = sharedPrefs.binusianId
        if (binusianId == null) {
            val binusianIdResp = api.getBinusianID()
            binusianId = binusianIdResp.data!!.binusianId
            sharedPrefs.binusianId = binusianId
        }

        // 2. Get student profile
        val profileResp =
            api.getStudentProfile(BinusStudentProfileRequest(binusianId).encryptParams(cryptoService))
        validateResponseStatus(profileResp)

        return profileResp.data!!
    }

    suspend fun getStudentTermList(student: Student): List<BinusStudentTerm> {
        val req = BinusStudentTermRequest(
            student.acadCareer,
            student.binusianId,
            student.institution,
            student.studentType
        ).encryptParams(cryptoService)
        val res = api.getStudentTermList(req)
        validateResponseStatus(res)

        return res.data!!.termList
    }

    suspend fun getStudentCourses(student: Student): List<BinusStudentCourse> {
        val req = BinusCourseDataRequest(
            student.acadCareer,
            student.binusianId,
            student.strm,
            student.nim
        ).encryptParams(cryptoService)
        val res = api.getStudentCourses(req)
        validateResponseStatus(res)

        return res.data!!.courseList
    }

    suspend fun getCourseSchedule(student: Student): List<BinusCourseClass> {
        val req = BinusCourseScheduleRequest(
            student.acadCareer,
            student.binusianId,
            student.institution,
            student.strm,
            student.studentType
        ).encryptParams(cryptoService)
        val res = api.getCourseSchedule(req)
        validateResponseStatus(res)

        return res.data!!.listEventCourse
    }

    suspend fun getForumThreads(
        student: Student,
        courseClass: CourseClass
    ): List<BinusForumThread> {
        var attempt = 0

        lateinit var result: List<BinusForumThread>

        while (true) {
            try {
                val req = BinusForumThreadRequest(
                    student.acadCareer,
                    courseClass.id,
                    student.binusianId,
                    "1",
                    student.strm,
                    student.studentType
                ).encryptParam(cryptoService)
                val res = api.getForumThreads(req)
                validateResponseStatus(res)

                result = res.data!!.listForumTopicThread!!
                break
            } catch (ex: HttpException) {
                attempt += 1
                if (attempt > 5) {
                    throw ex
                }
            }
        }

        return result
    }

    suspend fun getForumThreadPosts(
        student: Student,
        forumThread: ForumThread
    ): List<BinusForumThreadPost> {
        var page = 1
        val req = BinusForumThreadPostRequest(
            student.acadCareer,
            student.strm,
            student.studentType,
            forumThread.id,
            page.toString()
        ).encryptParams(cryptoService)

        val finalList = arrayListOf<BinusForumThreadPost>()

        while (true) {
            var done = false
            var attempt = 0
            while (true) {
                try {
                    val res = api.getForumThreadPosts(req)
                    validateResponseStatus(res)

                    val currentList = res.data!!.listForumTopicThreadPost
                    if (currentList != null && currentList.isNotEmpty()) finalList.addAll(
                        currentList
                    )
                    else {
                        done = true
                        break
                    }

                    page += 1
                    req.page = cryptoService.encryptParam(page.toString())
                    break
                } catch (ex: Exception) {
                    attempt += 1
                    if (attempt > 5) {
                        throw ex
                    }
                }
            }
            if (done) break
        }

        return finalList
    }

    suspend fun replyForum(
        student: Student,
        forumThread: ForumThread,
        attachment: MultipartBody.Part?
    ) {
        val res = api.replyForum(
            forumTypeId = createPartFromString(cryptoService.encryptParam(forumThread.forumTypeId)),
            message = createPartFromString(forumThread.replyMessage!!),
            postReplyTo = createPartFromString(cryptoService.encryptParam(forumThread.firstPostId)),
            studentType = createPartFromString(cryptoService.encryptParam(student.studentType)),
            subject = createPartFromString(forumThread.subject),
            threadId = createPartFromString(cryptoService.encryptParam(forumThread.id)),
            userId = createPartFromString(cryptoService.encryptParam(student.nim)),
            attachment = attachment
        )
        validateResponseStatus(res)
    }

    private fun createPartFromString(value: String) = RequestBody.create(MultipartBody.FORM, value)
}