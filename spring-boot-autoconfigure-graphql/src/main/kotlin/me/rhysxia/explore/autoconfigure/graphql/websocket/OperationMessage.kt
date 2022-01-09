package me.rhysxia.explore.autoconfigure.graphql.websocket

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

data class OperationMessage(
  @JsonProperty("type") val type: String,
  @JsonInclude(JsonInclude.Include.NON_NULL) @JsonProperty("payload") val payload: Any? = null,
  @JsonInclude(JsonInclude.Include.NON_NULL) @JsonProperty("id", required = false) val id: String? = ""
)