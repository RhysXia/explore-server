package me.rhysxia.explore.autoconfigure.graphql.annotations

import org.springframework.core.annotation.AliasFor

@GraphqlHandler("Subscription")
annotation class GraphqlSubscriptionHandler(
  @get:AliasFor(annotation = GraphqlHandler::class)
  val fieldName: String = ""
)