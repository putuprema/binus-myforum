package xyz.purema.binusmyforum.domain.repository

import android.net.Uri
import xyz.purema.binusmyforum.domain.model.forum.ForumThread
import xyz.purema.binusmyforum.domain.model.forum.GslcForumQueryResult

interface ForumRepository {
    suspend fun getGslcForum(includeReplied: Boolean = true): GslcForumQueryResult
    suspend fun syncForumData()
    suspend fun replyForum(forum: ForumThread, attachmentUri: Uri?)
}