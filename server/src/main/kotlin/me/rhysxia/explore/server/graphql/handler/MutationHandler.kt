package me.rhysxia.explore.server.graphql.handler

import me.rhysxia.explore.autoconfigure.graphql.annotations.GraphqlHandler
import me.rhysxia.explore.autoconfigure.graphql.annotations.GraphqlInput
import me.rhysxia.explore.autoconfigure.graphql.annotations.GraphqlMutation
import me.rhysxia.explore.server.exception.AuthenticationException
import me.rhysxia.explore.server.graphql.resolver.CurrentUser
import me.rhysxia.explore.server.po.TokenPo
import me.rhysxia.explore.server.po.UserPo
import me.rhysxia.explore.server.service.TokenService

@GraphqlMutation
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
