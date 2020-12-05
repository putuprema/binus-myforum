package xyz.purema.binusmyforum.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "course_class_schedule",
    foreignKeys = [ForeignKey(
        entity = CourseClassDb::class,
        parentColumns = ["id"],
        childColumns = ["courseClassId"],
        onUpdate = CASCADE,
        onDelete = CASCADE
    )]
)
data class CourseClassScheduleDb(
    @ColumnInfo(index = true) val courseClassId: String,
    val scheduleType: String,
    val date: LocalDate,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
)