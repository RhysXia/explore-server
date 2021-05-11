package me.rhysxia.explore.server.service.impl

import kotlinx.coroutines.flow.Flow
import me.rhysxia.explore.server.manager.UserManager
import me.rhysxia.explore.server.po.UserPo
import me.rhysxia.explore.server.service.UserService
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(private val userManager: UserManager) : UserService {
  override fun findAll(): Flow<UserPo> {
    return userManager.findAll()
  }
}