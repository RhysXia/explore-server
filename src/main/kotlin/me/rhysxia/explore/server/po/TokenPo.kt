package me.rhysxia.explore.server.po

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("sys_token")
data class TokenPo(
    @Id
    val id: String?,

    val userId: Long,

    val createdAt: Instant,
    val updatedAt: Instant
)
