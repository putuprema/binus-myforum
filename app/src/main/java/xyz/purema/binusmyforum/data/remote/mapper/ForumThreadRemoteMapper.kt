package xyz.purema.binusmyforum.data.remote.mapper

import xyz.purema.binusmyforum.data.remote.model.response.forumthread.BinusForumThread
import xyz.purema.binusmyforum.domain.EntityMapper
import xyz.purema.binusmyforum.domain.model.forum.ForumThread
import javax.inject.Inject

class ForumThreadRemoteMapper
@Inject constructor() : EntityMapper<BinusForumThread, ForumThread> {
    override fun mapFromEntity(entity: BinusForumThread): ForumThread {
        return ForumThread(
            id = entity.threadId,
            courseClassId = "",
            createdAt = entity.createDate.toLocalDate(),
            status = entity.status,
            subject = entity.threadTitle,
            creator = entity.threadCreatorName,
            creatorMessage = "",
            firstPostId = "",
            forumTypeId = "1",
            attachmentLink = "",
            replyMessage = ""
        )
    }

    override fun mapToEntity(domainModel: ForumThread): BinusForumThread {
        return BinusForumThread(
            threadId = domainModel.id,
            threadTitle = domainModel.subject,
            threadCreatorName = domainModel.creator,
            threadCreatorID = "",
            createDate = domainModel.createdAt.atStartOfDay(),
            status = domainModel.status
        )
    }

    override fun mapFromEntities(entities: List<BinusForumThread>): List<ForumThread> {
        return entities.map { mapFromEntity(it) }
    }
}