package me.rhysxia.explore.server.repository

import me.rhysxia.explore.server.po.TokenPo
import org.springframework.data.repository.kotlin.CoroutineSortingRepository

interface TokenRepository : CoroutineSortingRepository<TokenPo, String> {
  suspend fun findOneByToken(token: String): TokenPo?
}
