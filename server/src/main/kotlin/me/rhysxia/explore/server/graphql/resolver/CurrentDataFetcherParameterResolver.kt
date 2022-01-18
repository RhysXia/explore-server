package me.rhysxia.explore.server.graphql.resolver

import graphql.schema.DataFetchingEnvironment
import me.rhysxia.explore.autoconfigure.graphql.getServerRequest
import me.rhysxia.explore.autoconfigure.graphql.getWebSocketSession
import me.rhysxia.explore.autoconfigure.graphql.interfaces.GraphqlDataFetcherParameterResolver
import me.rhysxia.explore.server.po.TokenPo
import me.rhysxia.explore.server.po.UserPo
import me.rhysxia.explore.server.po.UserStatus
import org.springframework.stereotype.Component
import java.time.Instant
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.javaType

@Component
class CurrentDataFetcherParameterResolver : GraphqlDataFetcherParameterResolver<Any> {
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

  override fun resolve(dfe: DataFetchingEnvironment, parameter: KParameter): Any {
    val ctx = dfe.graphQlContext

    val request = ctx.getServerRequest()
    val session = ctx.getWebSocketSession()
    throw RuntimeException("aaa")

    return UserPo(
      1,
      "username",
      "password",
      "nickname",
      "avatar",
      "email",
      UserStatus.ACTIVATED,
      "bio",
      Instant.now(),
      Instant.now(),
      Instant.now()
    )
  }
}