package xyz.purema.binusmyforum.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import xyz.purema.binusmyforum.domain.model.ClassType

@Entity(
    tableName = "course_class",
    foreignKeys = [ForeignKey(
        entity = CourseDb::class,
        parentColumns = ["id"],
        childColumns = ["courseId"],
        onUpdate = CASCADE,
        onDelete = CASCADE
    )]
)
data class CourseClassDb(
    @PrimaryKey(autoGenerate = false) val id: String,
    @ColumnInfo(index = true) val courseId: String,
    val classSection: String,
    val classType: ClassType
)