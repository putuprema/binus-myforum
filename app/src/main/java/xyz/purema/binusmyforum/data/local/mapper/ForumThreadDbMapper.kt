package xyz.purema.binusmyforum.data.local.mapper

import xyz.purema.binusmyforum.data.local.model.ForumThreadDb
import xyz.purema.binusmyforum.domain.EntityMapper
import xyz.purema.binusmyforum.domain.model.forum.ForumThread
import javax.inject.Inject

class ForumThreadDbMapper
@Inject constructor() : EntityMapper<ForumThreadDb, ForumThread> {
    override fun mapFromEntity(entity: ForumThreadDb): ForumThread {
        return ForumThread(
            id = entity.id,
            courseClassId = entity.courseClassId,
            createdAt = entity.createdAt,
            status = entity.status,
            subject = entity.subject,
            creator = entity.creator,
            creatorMessage = entity.creatorMessage,
            firstPostId = entity.firstPostId,
            forumTypeId = entity.forumTypeId,
            attachmentLink = entity.attachmentLink,
            replyMessage = entity.replyMessage
        )
    }

    override fun mapToEntity(domainModel: ForumThread): ForumThreadDb {
        return ForumThreadDb(
            id = domainModel.id,
            courseClassId = domainModel.courseClassId,
            createdAt = domainModel.createdAt,
            status = domainModel.status,
            subject = domainModel.subject,
            creator = domainModel.creator,
            creatorMessage = domainModel.creatorMessage,
            firstPostId = domainModel.firstPostId,
            attachmentLink = domainModel.attachmentLink,
            replyMessage = domainModel.replyMessage,
            forumTypeId = domainModel.forumTypeId
        )
    }

    override fun mapFromEntities(entities: List<ForumThreadDb>): List<ForumThread> {
        return entities.map { mapFromEntity(it) }
    }
}