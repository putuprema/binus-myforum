package xyz.purema.binusmyforum.domain.model.forum

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate

@Parcelize
data class GslcForum(
    var classCode: String,
    var classType: String,
    var course: String,
    var startDate: LocalDate,
    var dueDate: LocalDate,
    var forumThread: ForumThread? = null
) : Parcelable