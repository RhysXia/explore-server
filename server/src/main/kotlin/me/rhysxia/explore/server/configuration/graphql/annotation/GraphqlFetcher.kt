package me.rhysxia.explore.server.configuration.graphql.annotation

import org.springframework.stereotype.Component

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Component
annotation class GraphqlFetcher(
  val parentType: String = "",
  val fieldName: String = ""
)
