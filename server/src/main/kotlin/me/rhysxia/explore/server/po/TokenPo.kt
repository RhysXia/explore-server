package me.rhysxia.explore.server.po

import java.time.Instant

data class TokenPo(
    val id: String,

    val userId: Long,

    val createdAt: Instant,
)
