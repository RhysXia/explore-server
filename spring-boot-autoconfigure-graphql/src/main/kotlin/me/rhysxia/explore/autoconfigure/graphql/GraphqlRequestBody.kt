package me.rhysxia.explore.autoconfigure.graphql

data class GraphqlRequestBody(
    val variables: Map<String, Any> = emptyMap(),
    val extensions: Map<String, Any> = emptyMap(),
    val operationName: String?,
    val query: String
)