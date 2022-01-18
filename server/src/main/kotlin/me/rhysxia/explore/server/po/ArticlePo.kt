package me.rhysxia.explore.server.po

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("article")
data class ArticlePo(
  @Id
  val id: Long?,
  val title: String,
  val content: String,
  val contentType: ContentType,
  val createdAt: Instant,
  val updatedAt: Instant,
  val authorId: Long,
  val categoryId: Long,
)
