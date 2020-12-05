package xyz.purema.binusmyforum.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student")
data class StudentDb(
    @PrimaryKey(autoGenerate = false) val nim: String,
    val name: String,
    val binusianId: String,
    val email: String,
    val acadCareer: String,
    val institution: String,
    val studentType: String,
    var strm: String
)