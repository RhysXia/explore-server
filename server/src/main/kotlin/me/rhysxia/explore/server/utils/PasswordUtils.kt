package me.rhysxia.explore.server.utils

object PasswordUtils {
  fun encode(password: String): String {
    return password
  }

  fun match(password: String, newPassword: String): Boolean {
    return encode(password) == newPassword
  }
}