package me.rhysxia.explore.server.dto

import me.rhysxia.explore.server.po.PermissionPo
import me.rhysxia.explore.server.po.RolePo

data class AuthRole(
    val role: RolePo,
    val permissions: List<PermissionPo>
)