package me.rhysxia.niemandsland.server.repository

import kotlinx.coroutines.flow.Flow
import me.rhysxia.niemandsland.server.po.UserPo

interface UserRepository {
  fun findAll(): Flow<UserPo>
}