package me.rhysxia.explore.autoconfigure.graphql.annotations

import org.springframework.stereotype.Component

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Component
annotation class GraphqlInput(
  val name: String
)
