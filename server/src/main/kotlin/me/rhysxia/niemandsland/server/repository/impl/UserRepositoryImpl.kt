package me.rhysxia.niemandsland.server.repository.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import me.rhysxia.niemandsland.server.po.UserPo
import me.rhysxia.niemandsland.server.repository.UserRepository
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(private val template: R2dbcEntityTemplate, private val tableNameMapping: TableNameMapping) : UserRepository {
  override fun findAll(): Flow<UserPo> {
    return template.select(UserPo::class.java).from(tableNameMapping.user).all().asFlow()
  }
}