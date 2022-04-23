package me.rhysxia.explore.server.po

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("comment")
data class CommentPo(
    @Id
    val id: Long?,
    val content: String,
    val contentType: ContentType,
    val createdAt: Instant,
    val updatedAt: Instant,
    val authorId: Long,
    val articleId: Long,
    val parentId: Long?
)
