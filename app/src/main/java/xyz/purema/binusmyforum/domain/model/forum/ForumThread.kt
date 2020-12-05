package xyz.purema.binusmyforum.domain.model.forum

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class ForumThread(
    var id: String,
    var courseClassId: String,
    var createdAt: LocalDate,
    var status: String,
    var subject: String,
    var creator: String,
    var creatorMessage: String,
    var firstPostId: String,
    val forumTypeId: String,
    var attachmentLink: String?,
    var replyMessage: String?,
) : Parcelable