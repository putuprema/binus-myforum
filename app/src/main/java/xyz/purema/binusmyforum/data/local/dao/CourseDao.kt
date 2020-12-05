package xyz.purema.binusmyforum.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import xyz.purema.binusmyforum.data.local.model.CourseDb

@Dao
interface CourseDao {
    @Query("SELECT * FROM course WHERE id = :id")
    suspend fun getById(id: String): CourseDb?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun save(vararg courses: CourseDb)
}