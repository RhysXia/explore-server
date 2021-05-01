package me.rhysxia.niemandsland.server.controller

import me.rhysxia.niemandsland.server.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

  @GetMapping()
  fun findAll() = userService.findAll()
}