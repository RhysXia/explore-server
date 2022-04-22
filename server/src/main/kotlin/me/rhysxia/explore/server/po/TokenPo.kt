package me.rhysxia.explore.server.po

import java.time.Instant

data class TokenPo(
  val id: Long?,

  val token: String,

  val userId: Long,

  val createdAt: Instant,
  val updatedAt: Instant
)
