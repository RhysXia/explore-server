package me.rhysxia.niemandsland.server.service

import kotlinx.coroutines.flow.Flow
import me.rhysxia.niemandsland.server.po.UserPo

interface UserService {
    fun findAll(): Flow<UserPo>
}