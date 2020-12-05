package xyz.purema.binusmyforum.data.remote.model.response.forumthread

import java.time.LocalDateTime

class BinusForumThread(
    var threadId: String,
    var threadTitle: String,
    var threadCreatorName: String,
    var threadCreatorID: String,
    var createDate: LocalDateTime,
    var status: String
)
