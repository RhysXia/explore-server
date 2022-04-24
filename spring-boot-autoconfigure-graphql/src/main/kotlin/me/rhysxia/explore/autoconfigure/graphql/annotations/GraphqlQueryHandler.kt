package me.rhysxia.explore.autoconfigure.graphql.annotations

import org.springframework.core.annotation.AliasFor

@GraphqlHandler("Query")
annotation class GraphqlQueryHandler(
  @get:AliasFor(annotation = GraphqlHandler::class, value = "fieldName")
  val fieldName: String = ""
)
