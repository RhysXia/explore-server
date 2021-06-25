package me.rhysxia.explore.server.service

import kotlinx.coroutines.flow.Flow
import me.rhysxia.explore.server.po.UserPo

interface UserService {
  fun findAllById(ids: Set<Long>): Flow<UserPo>


}
