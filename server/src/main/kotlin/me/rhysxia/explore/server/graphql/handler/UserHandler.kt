package me.rhysxia.explore.server.graphql.handler

import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactor.awaitSingle
import me.rhysxia.explore.autoconfigure.graphql.annotations.*
import me.rhysxia.explore.autoconfigure.graphql.getRequestContainer
import me.rhysxia.explore.server.exception.AuthenticationException
import me.rhysxia.explore.server.filter.AuthFilter
import me.rhysxia.explore.server.graphql.resolver.CurrentUser
import me.rhysxia.explore.server.po.ArticlePo
import me.rhysxia.explore.server.po.TokenPo
import me.rhysxia.explore.server.po.UserPo
import me.rhysxia.explore.server.service.ArticleService
import me.rhysxia.explore.server.service.TokenService
import org.springframework.data.domain.Pageable

@GraphqlData("User")
class UserHandler(
    private val articleService: ArticleService,
    private val tokenService: TokenService
) {
    @GraphqlMutationHandler
    suspend fun login(
        @CurrentUser currentUser: UserPo?,
        @GraphqlInput("username") username: String,
        @GraphqlInput("password") password: String,
        def: DataFetchingEnvironment
    ): String {
        if (currentUser !== null) {
            throw AuthenticationException("用户已登录，请勿重复登录")
        }
        val authUser = tokenService.login(username, password)

        val session = def.graphQlContext.getRequestContainer().session.awaitSingle()

        session.attributes[AuthFilter.SESSION_KEY] = authUser

        return authUser.token.id
    }

    @GraphqlMutationHandler
    suspend fun logout(
        @CurrentUser token: TokenPo,
    ): Boolean {
        tokenService.logout(token)
        return true
    }

    @GraphqlQueryHandler
    fun currentUser(@CurrentUser user: UserPo) = user

    @GraphqlHandler
    fun articles(pageable: Pageable, dfe: DataFetchingEnvironment): Flow<ArticlePo> {
        val user = dfe.getSource<UserPo>()
        return articleService.findAllByAuthorId(user.id!!, pageable)
    }

}