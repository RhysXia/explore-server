package me.rhysxia.explore.server.initializer

import me.rhysxia.explore.server.po.UserPo
import me.rhysxia.explore.server.po.UserStatus
import me.rhysxia.explore.server.repository.UserRepository
import me.rhysxia.explore.server.utils.PasswordUtils
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class DynamicDataInitializer(private val userRepository: UserRepository) {
    suspend fun initialize() {
        val now = Instant.now()
        val admin = UserPo(
            null,
            "admin",
            PasswordUtils.encode("admin"),
            "admin",
            null,
            "xrs4433@outlook.com",
            UserStatus.ACTIVATED,
            null,
            now,
            now,
            null
        )

        userRepository.save(admin)
    }
}