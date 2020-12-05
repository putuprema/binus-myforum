package xyz.purema.binusmyforum.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "forum_thread",
    foreignKeys = [ForeignKey(
        entity = CourseClassDb::class,
        parentColumns = ["id"],
        childColumns = ["courseClassId"],
        onUpdate = CASCADE,
        onDelete = CASCADE
    )]
)
data class ForumThreadDb(
    @PrimaryKey(autoGenerate = false) val id: String,
    @ColumnInfo(index = true) val courseClassId: String,
    val createdAt: LocalDate,
    val status: String,
    val subject: String,
    val creator: String,
    var creatorMessage: String,
    val firstPostId: String,
    val forumTypeId: String,
    var attachmentLink: String? = null,
    var replyMessage: String? = null
)