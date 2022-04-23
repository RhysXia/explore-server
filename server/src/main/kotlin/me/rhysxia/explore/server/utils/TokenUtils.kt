package me.rhysxia.explore.server.utils

import java.util.*

object TokenUtils {
    fun generateToken(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }
}