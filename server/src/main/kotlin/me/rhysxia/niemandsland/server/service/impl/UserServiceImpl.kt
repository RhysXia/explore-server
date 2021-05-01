package me.rhysxia.niemandsland.server.service.impl

import kotlinx.coroutines.flow.Flow
import me.rhysxia.niemandsland.server.po.UserPo
import me.rhysxia.niemandsland.server.repository.UserRepository
import me.rhysxia.niemandsland.server.service.UserService
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(private val userRepository: UserRepository) : UserService {
    override fun findAll(): Flow<UserPo> {
        return userRepository.findAll()
    }
}