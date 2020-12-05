package xyz.purema.binusmyforum.domain.model.student

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Student(
    var nim: String,
    val name: String,
    val binusianId: String,
    val email: String,
    val acadCareer: String,
    val institution: String,
    val studentType: String,
    var strm: String
) : Parcelable