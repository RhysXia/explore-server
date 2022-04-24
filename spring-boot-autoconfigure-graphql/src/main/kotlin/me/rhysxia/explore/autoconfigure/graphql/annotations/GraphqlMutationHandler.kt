package me.rhysxia.explore.autoconfigure.graphql.annotations

import org.springframework.core.annotation.AliasFor

@GraphqlHandler("Mutation")
annotation class GraphqlMutationHandler(
  @get:AliasFor(annotation = GraphqlHandler::class)
  val fieldName: String = ""
)
