package me.rhysxia.explore.server.service

import me.rhysxia.explore.server.dto.AuthUser
import me.rhysxia.explore.server.po.TokenPo

interface TokenService {
    /**
     * 获取登录用户信息
     */
    suspend fun findCurrentUserByToken(token: String): AuthUser?

    suspend fun login(username: String, password: String): AuthUser

    suspend fun logout(token: TokenPo)
}
