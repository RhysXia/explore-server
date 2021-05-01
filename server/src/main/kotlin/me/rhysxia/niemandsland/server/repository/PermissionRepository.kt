package me.rhysxia.niemandsland.server.repository

import kotlinx.coroutines.flow.Flow
import me.rhysxia.niemandsland.server.po.PermissionPo

interface PermissionRepository {

  fun findAllByRoleId(roleId: Long): Flow<PermissionPo>
}