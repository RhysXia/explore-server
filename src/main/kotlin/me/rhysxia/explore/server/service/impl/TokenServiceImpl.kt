package me.rhysxia.explore.server.service.impl

import me.rhysxia.explore.server.dto.AuthUser
import me.rhysxia.explore.server.po.UserStatus
import me.rhysxia.explore.server.repository.TokenRepository
import me.rhysxia.explore.server.repository.UserRepository
import me.rhysxia.explore.server.service.TokenService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class TokenServiceImpl(private val tokenRepository: TokenRepository, private val userRepository: UserRepository) : TokenService {

    @Transactional
    override suspend fun findAuthUserByToken(token: String): AuthUser? {
        val tokenPo = tokenRepository.findById(token) ?: return null

        val now = Instant.now()

        val userPo = userRepository.findById(tokenPo.userId)

        if (userPo == null || userPo.status != UserStatus.ACTIVATED || now.epochSecond - tokenPo.updatedAt.epochSecond > 1 * 60 * 60 * 24) {
            // 删除token
            tokenRepository.delete(tokenPo)
            return null
        }

        val newToken = tokenPo.copy(updatedAt = now)

        tokenRepository.save(newToken)

        return AuthUser(newToken, userPo)

    }
}