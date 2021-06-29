package me.rhysxia.explore.server.graphql.handler

import me.rhysxia.explore.server.configuration.graphql.annotation.GraphqlData
import me.rhysxia.explore.server.configuration.graphql.annotation.GraphqlHandler
import me.rhysxia.explore.server.configuration.graphql.annotation.GraphqlInput
import me.rhysxia.explore.server.service.TokenService
import me.rhysxia.explore.server.service.UserService

@GraphqlData("Mutation")
class MutationHandler(private val tokenService: TokenService) {

  @GraphqlHandler
  suspend fun login(@GraphqlInput("username") username: String, @GraphqlInput("password") password: String): String {
    return tokenService.login(username, password)
  }

}
