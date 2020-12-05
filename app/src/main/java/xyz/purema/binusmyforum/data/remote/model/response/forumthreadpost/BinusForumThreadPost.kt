package xyz.purema.binusmyforum.data.remote.model.response.forumthreadpost

import java.time.LocalDateTime

class BinusForumThreadPost(
    var threadTitle: String,
    var createDate: LocalDateTime,
    var postId: String,
    var creatorId: String,
    var nama: String,
    var subject: String,
    var message: String,
    var uploadPath: String
)
