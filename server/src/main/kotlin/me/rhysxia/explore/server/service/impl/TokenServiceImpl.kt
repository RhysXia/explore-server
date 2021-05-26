package me.rhysxia.explore.server.service.impl

import me.rhysxia.explore.server.dto.AuthUser
import me.rhysxia.explore.server.repository.TokenRepository
import me.rhysxia.explore.server.service.TokenService
import org.springframework.stereotype.Service

@Service
class TokenServiceImpl(private val tokenRepository: TokenRepository) : TokenService {
    override suspend fun findAuthUserByToken(token: String): AuthUser? {

    }
}