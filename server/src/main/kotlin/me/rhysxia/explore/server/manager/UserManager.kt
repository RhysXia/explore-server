package me.rhysxia.explore.server.manager

import kotlinx.coroutines.flow.Flow
import me.rhysxia.explore.server.po.UserPo

interface UserManager {
  fun findAll(): Flow<UserPo>
}
