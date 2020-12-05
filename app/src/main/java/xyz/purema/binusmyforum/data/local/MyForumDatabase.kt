package xyz.purema.binusmyforum.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import xyz.purema.binusmyforum.data.local.converter.ClassTypeConverter
import xyz.purema.binusmyforum.data.local.converter.LocalDateConverter
import xyz.purema.binusmyforum.data.local.dao.*
import xyz.purema.binusmyforum.data.local.model.*

@Database(
    entities = [CourseDb::class, CourseClassDb::class, CourseClassScheduleDb::class, ForumThreadDb::class, StudentDb::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(LocalDateConverter::class, ClassTypeConverter::class)
abstract class MyForumDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun courseClassDao(): CourseClassDao
    abstract fun courseClassScheduleDao(): CourseClassScheduleDao
    abstract fun forumThreadDao(): ForumThreadDao
    abstract fun studentDao(): StudentDao

    companion object {
        val DATABASE_NAME = "myforum_db"
    }
}