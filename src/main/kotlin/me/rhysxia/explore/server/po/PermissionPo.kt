package me.rhysxia.explore.server.po

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("sys_permission")
data class PermissionPo(
    @Id
    val id: Long?,
    val path: String,
    val description: String
)
