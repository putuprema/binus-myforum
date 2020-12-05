package xyz.purema.binusmyforum.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import xyz.purema.binusmyforum.data.local.model.CourseClassDb

@Dao
interface CourseClassDao {
    @Query("SELECT * FROM course_class WHERE id = :id")
    suspend fun getById(id: String): CourseClassDb?

    @Query("SELECT * FROM course_class")
    suspend fun getAll(): List<CourseClassDb>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(vararg courseClasses: CourseClassDb)
}