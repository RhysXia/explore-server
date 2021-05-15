package me.rhysxia.explore.server.po

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("sys_user")
data class UserPo(
  @Id
  val id: Long?,
  val username: String,
  val password: String,
  val nickname: String,
  val avatar: String?,
  val email: String?,
  /**
   * 个人简介
   */
  val bio: String?,

  val createdAt: Instant,
  val updatedAt: Instant,
  val lastLoginAt: Instant,
)
