package me.rhysxia.explore.server.repository

import me.rhysxia.explore.server.po.TokenPo
import java.time.Duration

interface TokenRepository {
    /**
     * 获取token并刷新过期时间
     */
    suspend fun findOneById(id: String, expireTime: Duration): TokenPo?

    /**
     * 获取token
     */
    suspend fun findOneById(id: String): TokenPo?

    /**
     * 保存token
     */
    suspend fun save(tokenPo: TokenPo, expireTime: Duration)

    /**
     * 删除token
     */
    suspend fun delete(tokenPo: TokenPo)
}
