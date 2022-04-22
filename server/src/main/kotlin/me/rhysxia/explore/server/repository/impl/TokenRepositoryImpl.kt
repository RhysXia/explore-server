package me.rhysxia.explore.server.repository.impl

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import me.rhysxia.explore.server.po.TokenPo
import me.rhysxia.explore.server.repository.TokenRepository
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class TokenRepositoryImpl(private val objectReactiveRedisTemplate: ReactiveRedisTemplate<String, Any>) :
  TokenRepository {

  private final val KEY_PREFIX = TokenRepositoryImpl::class.qualifiedName

  override suspend fun findOneById(id: String, expireTime: Duration): TokenPo? {
    val value = objectReactiveRedisTemplate.opsForValue().getAndExpire(this.genKey(id), expireTime).awaitSingleOrNull()
    return value as TokenPo?
  }

  override suspend fun findOneById(id: String): TokenPo? {
    val value = objectReactiveRedisTemplate.opsForValue().get(this.genKey(id)).awaitSingle()
    return value as TokenPo?
  }

  override suspend fun save(tokenPo: TokenPo, expireTime: Duration) {
    objectReactiveRedisTemplate.opsForValue().set(this.genKey(tokenPo.token), tokenPo, expireTime).awaitSingle()
  }

  override suspend fun delete(tokenPo: TokenPo) {
    objectReactiveRedisTemplate.opsForValue().delete(this.genKey(tokenPo.token)).awaitSingle()
  }

  private fun genKey(key: String): String {
    return KEY_PREFIX + '_' + key
  }


}