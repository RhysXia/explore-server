package me.rhysxia.explore.server.service.impl

import me.rhysxia.explore.server.dto.AuthUser
import me.rhysxia.explore.server.exception.AuthenticationException
import me.rhysxia.explore.server.exception.ParameterException
import me.rhysxia.explore.server.po.TokenPo
import me.rhysxia.explore.server.po.UserStatus
import me.rhysxia.explore.server.repository.TokenRepository
import me.rhysxia.explore.server.repository.UserRepository
import me.rhysxia.explore.server.service.TokenService
import me.rhysxia.explore.server.utils.PasswordUtils
import me.rhysxia.explore.server.utils.TokenUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant

@Service
class TokenServiceImpl(private val tokenRepository: TokenRepository, private val userRepository: UserRepository) :
    TokenService {

    @Transactional
    override suspend fun findCurrentUserByToken(token: String): AuthUser? {
        // 七天过期
        val tokenPo = tokenRepository.findOneById(token, Duration.ofDays(7)) ?: return null

        val userPo = userRepository.findById(tokenPo.userId)

        if (userPo == null) {
            // 删除token
            tokenRepository.delete(tokenPo)
            return null
        }

        // 更新登录时间
        val newUserPo = userPo.copy(lastLoginAt = Instant.now())

        userRepository.save(newUserPo)

        return AuthUser(tokenPo, newUserPo)
    }

    @Transactional
    override suspend fun logout(token: TokenPo) {
        tokenRepository.delete(token)
    }

    @Transactional
    override suspend fun login(username: String, password: String): AuthUser {
        val user = userRepository.findByUsername(username) ?: throw ParameterException("用户名或者密码不正确")

        if (!PasswordUtils.match(password, user.password)) {
            throw ParameterException("用户名或者密码不正确")
        }

        if (user.status !== UserStatus.ACTIVATED) {
            throw AuthenticationException("用户状态错误")
        }

        val tokenId = TokenUtils.generateToken()

        val now = Instant.now()

        val token = TokenPo(tokenId, user.id!!, now)

        // 七天过期
        tokenRepository.save(token, Duration.ofDays(7))
        return AuthUser(token, user)
    }
}