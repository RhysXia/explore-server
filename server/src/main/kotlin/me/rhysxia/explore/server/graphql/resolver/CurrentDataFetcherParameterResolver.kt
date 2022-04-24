package me.rhysxia.explore.server.graphql.resolver

import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.mono
import me.rhysxia.explore.autoconfigure.graphql.getRequestContainer
import me.rhysxia.explore.autoconfigure.graphql.interfaces.GraphqlDataFetcherParameterResolver
import me.rhysxia.explore.server.dto.AuthUser
import me.rhysxia.explore.server.exception.AuthenticationException
import me.rhysxia.explore.server.po.TokenPo
import me.rhysxia.explore.server.po.UserPo
import me.rhysxia.explore.server.service.TokenService
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.javaType

@Component
class CurrentDataFetcherParameterResolver(private val tokenService: TokenService) :
    GraphqlDataFetcherParameterResolver<Any> {

    companion object {
        val SESSION_KEY = "__CURRENT_USER__"
    }

    override fun support(parameter: KParameter): Boolean {
        val currentUser = parameter.findAnnotation<CurrentUser>()
        if (currentUser === null) {
            return false
        }

        val javaType = parameter.type.javaType

        if (javaType !is Class<*>) {
            return false
        }

        return javaType.isAssignableFrom(UserPo::class.java) || javaType.isAssignableFrom(TokenPo::class.java)
    }

    override fun resolve(dfe: DataFetchingEnvironment, parameter: KParameter): Mono<Any> {
        return mono(Dispatchers.Unconfined) {
            val ctx = dfe.graphQlContext

            val requestContainer = ctx.getRequestContainer()

            var token = requestContainer.headers.getFirst(HttpHeaders.AUTHORIZATION)

            val currentUser = parameter.findAnnotation<CurrentUser>()!!

            if (token.isNullOrBlank()) {
                token = requestContainer.getQueryParam("token")
            }

            val session = requestContainer.session.awaitSingle()

            val authUser = if (token !== null && token.isNotBlank()) {
                val user = session.attributes[SESSION_KEY] as AuthUser?
                if(user !== null && user.token.id === token) {
                    user
                } else {
                    tokenService.findCurrentUserByToken(token)
                }
            } else {
                session.attributes[SESSION_KEY] as AuthUser?
            }

            if (authUser !== null) {
                val isUser = (parameter.type.javaType as Class<*>).isAssignableFrom(UserPo::class.java)

                if (isUser) {
                    return@mono authUser.user
                } else {
                    return@mono authUser.token
                }
            }

            if (currentUser.required || !parameter.type.isMarkedNullable) {
                throw AuthenticationException("Current Request is not authenticated.")
            }
            return@mono null

        }
    }
}