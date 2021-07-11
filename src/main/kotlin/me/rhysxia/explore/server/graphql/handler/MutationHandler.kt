package me.rhysxia.explore.server.graphql.handler

import me.rhysxia.explore.server.configuration.graphql.annotation.CurrentUser
import me.rhysxia.explore.server.configuration.graphql.annotation.GraphqlData
import me.rhysxia.explore.server.configuration.graphql.annotation.GraphqlHandler
import me.rhysxia.explore.server.configuration.graphql.annotation.GraphqlInput
import me.rhysxia.explore.server.exception.AuthenticationException
import me.rhysxia.explore.server.po.TokenPo
import me.rhysxia.explore.server.po.UserPo
import me.rhysxia.explore.server.service.TokenService

@GraphqlData("Mutation")
class MutationHandler(private val tokenService: TokenService) {

  @GraphqlHandler
  suspend fun login(
    @CurrentUser currentUser: UserPo?,
    @GraphqlInput("username") username: String,
    @GraphqlInput("password") password: String
  ): String {
    if (currentUser !== null) {
      throw AuthenticationException("用户已登录，请勿重复登录")
    }
    return tokenService.login(username, password)
  }

  @GraphqlHandler
  suspend fun logout(
    @CurrentUser token: TokenPo,
  ): Boolean {
    tokenService.logout(token)
    return true
  }

}
