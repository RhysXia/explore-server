package me.rhysxia.explore.server.configuration.graphql.controller

import com.fasterxml.jackson.annotation.JsonProperty

data class GraphqlRequestBody(
  @JsonProperty("variables") val variables: Map<String, Any> = emptyMap(),
  @JsonProperty("extensions") val extensions: Map<String, Any> = emptyMap(),
  @JsonProperty("operationName") val operationName: String?,
  @JsonProperty("query") val query: String
)