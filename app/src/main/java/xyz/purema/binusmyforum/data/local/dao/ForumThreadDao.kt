package xyz.purema.binusmyforum.data.local.dao

import androidx.room.*
import xyz.purema.binusmyforum.data.local.model.ForumThreadDb

@Dao
interface ForumThreadDao {
    @Query("SELECT * FROM forum_thread WHERE courseClassId = :courseClassId AND (date(createdAt) >= date(:startDate) AND date(createdAt) <= date(:endDate)) ORDER BY createdAt ASC")
    suspend fun getForumThreads(
        courseClassId: String,
        startDate: String,
        endDate: String
    ): List<ForumThreadDb>

    @Query("SELECT * FROM forum_thread WHERE id = :id")
    suspend fun getById(id: String): ForumThreadDb?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(vararg forumThreads: ForumThreadDb)

    @Update
    suspend fun update(forumThread: ForumThreadDb)
}