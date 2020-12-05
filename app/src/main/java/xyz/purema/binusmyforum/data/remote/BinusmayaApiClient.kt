package xyz.purema.binusmyforum.data.remote

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import xyz.purema.binusmyforum.data.remote.model.request.*
import xyz.purema.binusmyforum.data.remote.model.response.BinusBinusianIDResponse
import xyz.purema.binusmyforum.data.remote.model.response.BinusLoginResponse
import xyz.purema.binusmyforum.data.remote.model.response.BinusResponseEnvelope
import xyz.purema.binusmyforum.data.remote.model.response.BinusStudentProfileResponse
import xyz.purema.binusmyforum.data.remote.model.response.courseschedule.BinusCourseScheduleResponse
import xyz.purema.binusmyforum.data.remote.model.response.forumthread.BinusForumThreadResponse
import xyz.purema.binusmyforum.data.remote.model.response.forumthreadpost.BinusForumThreadPostResponse
import xyz.purema.binusmyforum.data.remote.model.response.studentcourse.BinusStudentCourseResponse
import xyz.purema.binusmyforum.data.remote.model.response.studentterm.BinusStudentTermResponse

interface BinusmayaApiClient {
    @POST("/oauth2/adapi/token")
    @FormUrlEncoded
    suspend fun getAuthToken(
        @Field("grant_type") grantType: BinusAuthRequest.GrantType,
        @Field("username") email: String? = null,
        @Field("password") password: String? = null,
        @Field("refresh_token") refreshToken: String? = null,
        @Field("client_id") clientId: String = "binus"
    ): BinusLoginResponse

    @POST("/api/Binusmaya/ProfileDB/V1/Profile/Profile/GetEmailBinusianID")
    suspend fun getBinusianID(@Body dummyBody: String = "{}"): BinusResponseEnvelope<BinusBinusianIDResponse>

    @POST("/api/BinusMobile/Student/V1/Login/Login/GetStudentLoginDetail")
    suspend fun getStudentProfile(@Body req: BinusStudentProfileRequest): BinusResponseEnvelope<BinusStudentProfileResponse>

    @POST("/api/Oracle/ScheduleDB/V1/Personal/Schedule/GetStudentScheduleTerm")
    suspend fun getStudentTermList(@Body req: BinusStudentTermRequest): BinusResponseEnvelope<BinusStudentTermResponse>

    @POST("/api/Oracle/General/V1/General/General/GetCourseDataStudent")
    suspend fun getStudentCourses(@Body req: BinusCourseDataRequest): BinusResponseEnvelope<BinusStudentCourseResponse>

    @POST("/api/BinusMobile/Student/V1/Schedule/Schedule/GetAllEventByCourse")
    suspend fun getCourseSchedule(@Body req: BinusCourseScheduleRequest): BinusResponseEnvelope<BinusCourseScheduleResponse>

    @POST("/api/BinusMobile/General/V1/Forum/Forum/GetForumTopicThread")
    suspend fun getForumThreads(@Body req: BinusForumThreadRequest): BinusResponseEnvelope<BinusForumThreadResponse>

    @POST("/api/BinusMobile/General/V1/Forum/Forum/GetForumThreadPost")
    suspend fun getForumThreadPosts(@Body req: BinusForumThreadPostRequest): BinusResponseEnvelope<BinusForumThreadPostResponse>

    @POST("/api/BinusMobile/General/V1/Forum/Forum/InsertForumPostV2")
    @Multipart
    suspend fun replyForum(
        @Part("forumTypeId") forumTypeId: RequestBody,
        @Part("message") message: RequestBody,
        @Part("postReplyTo") postReplyTo: RequestBody,
        @Part("studentType") studentType: RequestBody,
        @Part("subject") subject: RequestBody,
        @Part("threadId") threadId: RequestBody,
        @Part("userId") userId: RequestBody,
        @Part attachment: MultipartBody.Part? = null
    ): BinusResponseEnvelope<*>
}