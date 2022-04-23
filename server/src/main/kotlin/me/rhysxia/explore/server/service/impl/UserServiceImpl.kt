package me.rhysxia.explore.server.service.impl

import kotlinx.coroutines.flow.Flow
import me.rhysxia.explore.server.po.UserPo
import me.rhysxia.explore.server.repository.UserRepository
import me.rhysxia.explore.server.service.UserService
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(private val userRepository: UserRepository) : UserService {
    override fun findAllByIds(ids: Set<Long>): Flow<UserPo> {
        return userRepository.findAllById(ids)
    }
}