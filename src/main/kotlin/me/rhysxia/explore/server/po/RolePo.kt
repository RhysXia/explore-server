package me.rhysxia.explore.server.po

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("sys_role")
data class RolePo(
  @Id
  val id: Long?,
  val name: String,
  val description: String?
)
