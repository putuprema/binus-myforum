package xyz.purema.binusmyforum.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "course")
data class CourseDb(
    @PrimaryKey(autoGenerate = false) val id: String,
    val courseCode: String,
    val courseTitle: String
)