package me.rhysxia.explore.server.controller

import me.rhysxia.explore.server.dto.LoginUserDto
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/tokens")
class TokenController {

  @PostMapping
  fun create(@Valid @RequestBody loginUserDto: LoginUserDto) {
    println(loginUserDto.toString())
  }
}