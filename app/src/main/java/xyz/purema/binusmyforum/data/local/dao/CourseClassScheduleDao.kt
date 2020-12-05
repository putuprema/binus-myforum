package xyz.purema.binusmyforum.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import xyz.purema.binusmyforum.data.local.model.CourseClassScheduleDb

@Dao
interface CourseClassScheduleDao {
    @Query("SELECT * FROM course_class_schedule WHERE scheduleType = 'GSLC' AND julianday(date('now')) - julianday(date) BETWEEN 0 AND 7 ORDER BY date ASC")
    suspend fun getGslcSchedules(): List<CourseClassScheduleDb>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(vararg courseClassSchedules: CourseClassScheduleDb)
}