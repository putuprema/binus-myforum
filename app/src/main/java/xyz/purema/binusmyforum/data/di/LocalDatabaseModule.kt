package xyz.purema.binusmyforum.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import xyz.purema.binusmyforum.data.local.MyForumDatabase
import xyz.purema.binusmyforum.data.local.dao.*
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object LocalDatabaseModule {
    @Singleton
    @Provides
    fun myForumDatabase(@ApplicationContext context: Context): MyForumDatabase {
        return Room.databaseBuilder(
            context,
            MyForumDatabase::class.java,
            MyForumDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun courseDao(db: MyForumDatabase): CourseDao = db.courseDao()

    @Singleton
    @Provides
    fun courseClassDao(db: MyForumDatabase): CourseClassDao = db.courseClassDao()

    @Singleton
    @Provides
    fun courseClassScheduleDao(db: MyForumDatabase): CourseClassScheduleDao =
        db.courseClassScheduleDao()

    @Singleton
    @Provides
    fun forumThreadDao(db: MyForumDatabase): ForumThreadDao = db.forumThreadDao()

    @Singleton
    @Provides
    fun studentDto(db: MyForumDatabase): StudentDao = db.studentDao()
}