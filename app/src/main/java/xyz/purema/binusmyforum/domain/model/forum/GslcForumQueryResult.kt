package xyz.purema.binusmyforum.domain.model.forum

data class GslcForumQueryResult(
    val unreplied: Long,
    val list: List<GslcForum>
)