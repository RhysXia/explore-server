package me.rhysxia.explore.server.graphql.annotation

import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Component

@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Component
annotation class GraphqlQuery(
  @get:AliasFor(annotation = Component::class)
  val name: String = "", val description: String = ""
)
