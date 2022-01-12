package me.rhysxia.explore.autoconfigure.graphql.interfaces

import graphql.schema.DataFetchingEnvironment
import java.lang.reflect.Type

interface GraphqlDataFetcherParameterResolver<T: Any> {
  fun support(parameterType: Type): Boolean
  fun resolve(dfe: DataFetchingEnvironment, parameterType: Type): T
}