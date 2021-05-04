package me.rhysxia.explore.server.po

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("sys_user")
data class UserPo(
  @Id
  val id: Long?,
  val username: String,
  val password: String
)
