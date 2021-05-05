package me.rhysxia.explore.server.dto

import javax.validation.constraints.NotBlank

data class LoginUserDto(
  @field:NotBlank(message = "用户名不能为空")
  val username: String?,
  @field:NotBlank(message = "密码不能为空")
  val password: String?
)