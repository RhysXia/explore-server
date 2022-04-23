package me.rhysxia.explore.server.utils

import java.math.BigInteger
import java.security.MessageDigest

object PasswordUtils {
    fun encode(password: String): String {
        val bytes = password.toByteArray()
        val messageDigest = MessageDigest.getInstance("SHA")
        messageDigest.update(bytes)

        val sha = BigInteger(messageDigest.digest())
        return sha.toString(32)
    }

    fun match(password: String, newPassword: String): Boolean {
        return encode(password) == newPassword
    }
}