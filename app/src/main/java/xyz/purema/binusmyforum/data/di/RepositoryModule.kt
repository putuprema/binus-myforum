package xyz.purema.binusmyforum.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import xyz.purema.binusmyforum.data.local.MyForumDatabase
import xyz.purema.binusmyforum.data.local.dao.*
import xyz.purema.binusmyforum.data.local.mapper.ForumThreadDbMapper
import xyz.purema.binusmyforum.data.local.mapper.StudentDbMapper
import xyz.purema.binusmyforum.data.prefs.SharedPrefs
import xyz.purema.binusmyforum.data.remote.BinusmayaRemoteService
import xyz.purema.binusmyforum.data.remote.crypto.CryptoService
import xyz.purema.binusmyforum.data.remote.mapper.StudentRemoteMapper
import xyz.purema.binusmyforum.data.repository.ForumRepositoryImpl
import xyz.purema.binusmyforum.data.repository.StudentRepositoryImpl
import xyz.purema.binusmyforum.domain.repository.ForumRepository
import xyz.purema.binusmyforum.domain.repository.StudentRepository
import xyz.purema.binusmyforum.domain.utils.ApiUtils
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object RepositoryModule {
    @ExperimentalCoroutinesApi
    @Singleton
    @Provides
    fun forumRepository(
        @ApplicationContext context: Context,
        courseDao: CourseDao,
        courseClassDao: CourseClassDao,
        classScheduleDao: CourseClassScheduleDao,
        forumThreadDao: ForumThreadDao,
        forumThreadDbMapper: ForumThreadDbMapper,
        binusmayaRemoteService: BinusmayaRemoteService,
        studentDao: StudentDao,
        studentDbMapper: StudentDbMapper,
        apiUtils: ApiUtils
    ): ForumRepository {
        return ForumRepositoryImpl(
            context = context,
            courseDao = courseDao,
            courseClassDao = courseClassDao,
            classScheduleDao = classScheduleDao,
            forumThreadDao = forumThreadDao,
            forumThreadDbMapper = forumThreadDbMapper,
            binusmayaRemoteService = binusmayaRemoteService,
            studentDao = studentDao,
            studentDbMapper = studentDbMapper,
            apiUtils = apiUtils
        )
    }

    @Singleton
    @Provides
    fun studentRepository(
        @ApplicationContext context: Context,
        db: MyForumDatabase,
        courseClassDao: CourseClassDao,
        courseClassScheduleDao: CourseClassScheduleDao,
        courseDao: CourseDao,
        studentDao: StudentDao,
        studentDbMapper: StudentDbMapper,
        binusmayaRemoteService: BinusmayaRemoteService,
        studentRemoteMapper: StudentRemoteMapper,
        apiUtils: ApiUtils,
        cryptoService: CryptoService,
        sharedPrefs: SharedPrefs,
        forumRepository: ForumRepository
    ): StudentRepository {
        return StudentRepositoryImpl(
            context = context,
            db = db,
            courseClassDao = courseClassDao,
            courseClassScheduleDao = courseClassScheduleDao,
            courseDao = courseDao,
            studentDao = studentDao,
            studentDbMapper = studentDbMapper,
            binusmayaRemoteService = binusmayaRemoteService,
            studentRemoteMapper = studentRemoteMapper,
            cryptoService = cryptoService,
            apiUtils = apiUtils,
            sharedPrefs = sharedPrefs,
            forumRepository = forumRepository
        )
    }
}