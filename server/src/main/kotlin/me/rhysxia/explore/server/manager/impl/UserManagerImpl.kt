package me.rhysxia.explore.server.manager.impl

import kotlinx.coroutines.flow.Flow
import me.rhysxia.explore.server.annotation.Manage
import me.rhysxia.explore.server.manager.UserManager
import me.rhysxia.explore.server.po.UserPo
import me.rhysxia.explore.server.repository.UserRepository

@Manage
class UserManagerImpl(private val userRepository: UserRepository) : UserManager {
  override fun findAll(): Flow<UserPo> {
    return userRepository.findAll()
  }

  override suspend fun findOneByUsername(username: String): UserPo? {
    return userRepository.findOneByUsername(username)
  }
}