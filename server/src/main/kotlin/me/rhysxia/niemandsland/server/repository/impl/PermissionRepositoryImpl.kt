package me.rhysxia.niemandsland.server.repository.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import me.rhysxia.niemandsland.server.po.PermissionPo
import me.rhysxia.niemandsland.server.repository.PermissionRepository
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository

@Repository
class PermissionRepositoryImpl(private val template: R2dbcEntityTemplate) : PermissionRepository {
  override fun findAllByRoleId(roleId: Long): Flow<PermissionPo> {
    return template.select(PermissionPo::class.java).all().asFlow()
  }
}