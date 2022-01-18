package me.rhysxia.explore.server.graphql.resolver

import graphql.schema.DataFetchingEnvironment
import me.rhysxia.explore.autoconfigure.graphql.interfaces.GraphqlDataFetcherParameterResolver
import me.rhysxia.explore.server.po.TokenPo
import me.rhysxia.explore.server.po.UserPo
import org.springframework.stereotype.Component
import java.lang.reflect.Type

@Component
class CurrentDataFetcherParameterResolver : GraphqlDataFetcherParameterResolver<Any> {
  override fun support(parameterType: Type): Boolean {
    if (parameterType is Class<*>) {
      val currentUser = parameterType.getAnnotation(CurrentUser::class.java)
      if (currentUser === null) {
        return false
      }

      return parameterType.isAssignableFrom(UserPo::class.java) || parameterType.isAssignableFrom(TokenPo::class.java)
    }

    return false
  }

  override fun resolve(dfe: DataFetchingEnvironment, parameterType: Type): Any {
    TODO("Not yet implemented")
  }
}