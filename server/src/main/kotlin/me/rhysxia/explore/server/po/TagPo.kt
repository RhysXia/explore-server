package me.rhysxia.explore.server.po

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("tag")
data class TagPo(
  @Id
  val id: Long?,
  val name: String,
  val createdAt: Instant,
  val updatedAt: Instant,
)
