package xyz.purema.binusmyforum.data.local.dao

import androidx.room.*
import xyz.purema.binusmyforum.data.local.model.StudentDb

@Dao
interface StudentDao {
    @Query("SELECT * FROM student LIMIT 1")
    suspend fun get(): StudentDb?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun save(student: StudentDb)

    @Update
    suspend fun update(student: StudentDb)
}