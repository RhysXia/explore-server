package me.rhysxia.explore.server.configuration.graphql

data class GraphqlRequestBody(
  val query: String?,
  val variables: Map<String, Any> = emptyMap(),
  val operationName: String?
)