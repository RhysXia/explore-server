package me.rhysxia.explore.server.dto

import me.rhysxia.explore.server.po.UserPo

data class AuthUser(
    val user: UserPo,
    val roles: List<AuthRole>
)
